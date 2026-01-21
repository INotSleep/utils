package com.inotsleep.utils.i18n;

import com.google.gson.*;
import com.inotsleep.utils.logging.Logger;
import com.inotsleep.utils.i18n.config.LangEntry;
import com.inotsleep.utils.i18n.config.LangFile;
import com.inotsleep.utils.logging.LoggingManager;
import com.inotsleep.utils.INSUtils;
import com.inotsleep.utils.objects.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class I18n {
    private static final String API_ENDPOINT_HOST = "https://inotsleep.com/";
    private static I18n instance;

    private final INSUtils plugin;
    private final Logger logger;

    private HttpClient httpClient;
    private I18nConfig config;

    private final Map<String, I18nPluginLang> pluginLangMap = new ConcurrentHashMap<>();
    private final Map<String, I18nConsumer> consumers = new ConcurrentHashMap<>();
    private Set<String> remoteProjects;
    private List<String> supportedLanguages;

    private final Set<String> queued = ConcurrentHashMap.newKeySet();
    private final Set<String> inFlight = ConcurrentHashMap.newKeySet();
    private final Set<String> fetched = ConcurrentHashMap.newKeySet();

    private volatile boolean projectsReady = false;

    private final List<I18nConsumer> unFetchedConsumers = new ArrayList<>();

    AtomicBoolean reloadBlocked = new AtomicBoolean(false);

    private I18n(INSUtils plugin) {
        this.plugin = plugin;
        logger = LoggingManager.getPrefixedLogger("[I18n] ");
    }

    public void shutdown() {
        getHttpClient().close();
    }

    private HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = HttpClient
                    .newBuilder()
                    .connectTimeout(Duration.of(10000, ChronoUnit.MILLIS))
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();
        }

        return httpClient;
    }

    public void registerConsumer(I18nConsumer consumer) {
        consumers.put(consumer.getSlug(), consumer);

        String slug = consumer.getSlug();

        if (!projectsReady) {
            if (queued.add(slug)) {
                logger.debug("Queued i18n fetch for " + slug);
            }
            return;
        }

        scheduleFetch(slug);
    }

    private CompletableFuture<Void> scheduleFetch(String slug) {
        if (fetched.contains(slug)) {
            return null;
        }

        if (!inFlight.add(slug)) {
            return null;
        }

        return CompletableFuture.runAsync(() -> {
            try {
                I18nConsumer consumer = consumers.get(slug);
                if (consumer != null) {
                    fetchTranslationFiles(consumer);
                    fetched.add(slug);
                }
            } finally {
                inFlight.remove(slug);
            }
        }, INSUtils.getInstance().getExecutor());
    }


    public @Nullable LangEntry getEntry(String key, String lang, String plugin) {
        I18nPluginLang pluginLang = pluginLangMap.get(plugin);
        if (pluginLang == null) return null;

        LangEntry entry = pluginLang.getLangEntry(key, lang);
        if (entry == null) {
            if (!config.isFallbackEnabled() && Objects.equals(lang, config.getFallbackLanguage())) return null;

            entry = pluginLang.getLangEntry(key, config.getFallbackLanguage());
        }
        return entry;
    }

    public @NotNull String getString(String key, String lang, String plugin) {
        LangEntry entry = getEntry(key, lang, plugin);
        if (entry == null || entry.isList()) {
            if (entry == null) logger.debug("No entry found for key: " + key + ", lang: " + lang + ", plugin: " + plugin);
            else logger.debug("Entry for key: " + key + ", lang: " + lang + ", plugin: " + plugin + " is not String");

            return plugin + ":" + key;
        }

        return entry.getValue();
    }

    public @NotNull List<String> getStringList(String key, String lang, String plugin) {
        LangEntry entry = getEntry(key, lang, plugin);
        if (entry == null || entry.isString()) {
            if (entry == null) logger.debug("No entry found for key: " + key + ", lang: " + lang + ", plugin: " + plugin);
            else logger.debug("Entry for key: " + key + ", lang: " + lang + ", plugin: " + plugin + " is not List");

            return List.of(plugin + ":" + key);
        }

        return entry.getListValue();
    }

    public String getGlobalString(String key, String lang) {
        return getString(key, lang, "global");
    }

    public List<String> getGlobalStringList(String key, String lang) {
        return getStringList(key, lang, "global");
    }

    public void reload() {
        if (reloadBlocked.getAndSet(true)) {
            throw new IllegalStateException("Called while blocked reload");
        }

        try {
            reloadUnsafe();
        } finally {
            reloadBlocked.set(false);
        }
    }

    private void reloadUnsafe() {
        projectsReady = false;

        pluginLangMap.clear();
        config = new I18nConfig();

        config.reload();

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        File langFolder = new File(dataFolder, "lang");
        if (!langFolder.exists()) langFolder.mkdirs();

        File[] files = langFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) continue;

                String pluginName = file.getName();
                I18nPluginLang pluginLang = new I18nPluginLang(pluginName);
                pluginLangMap.put(pluginName, pluginLang);

                File[] langFiles = file.listFiles();
                if (langFiles == null) continue;

                for (File langFile : langFiles) {
                    if (!langFile.isFile()) continue;
                    if (!langFile.getName().endsWith(".yml")) continue;

                    LangFile langFileObject = new LangFile(langFile);

                    pluginLang.addLang(langFile.getName().replace(".yml", ""), langFileObject);
                }
            }
        }

        CompletableFuture<Set<String>> remoteProjectsFuture = CompletableFuture.supplyAsync(
                this::getProjects,
                INSUtils.getInstance().getExecutor()
        );
        CompletableFuture<List<String>> supportedLanguagesFuture = CompletableFuture.supplyAsync(
                this::getLanguages,
                INSUtils.getInstance().getExecutor()
        );

        CompletableFuture.allOf(remoteProjectsFuture, supportedLanguagesFuture).join();

        remoteProjects = remoteProjectsFuture.resultNow();
        logger.info("Loaded " + remoteProjects.size() + " remote projects");
        supportedLanguages = supportedLanguagesFuture.resultNow();
        logger.info("Supporting " + supportedLanguages.size() + " language(s)");

        List<CompletableFuture<Void>> fetchFutures = new ArrayList<>();

        projectsReady = true;

        fetched.clear();

        for (String slug : queued) {
            fetchFutures.add(scheduleFetch(slug));
        }
        queued.clear();

        for (String slug : consumers.keySet()) {
            fetchFutures.add(scheduleFetch(slug));
        }

        CompletableFuture.allOf(
                fetchFutures
                        .stream()
                        .filter(Objects::nonNull)
                        .toArray(CompletableFuture[]::new)
        ).join();
    }
    public void fetchTranslationFiles(I18nConsumer consumer) {
        if (consumer == null) {
            return;
        }

        String projectId = consumer.getSlug();
        if (!remoteProjects.contains(projectId)) {
            logger.info("Skipping fetching translations for project " + projectId + ", as it does not exist on remote");
            return;
        }

        File dataFolder = this.plugin.getDataFolder();
        File langFolder = new File(dataFolder, "lang");
        File projectLangFolder = new File(langFolder, projectId);

        I18nPluginLang pluginLang = pluginLangMap.computeIfAbsent(consumer.getSlug(), I18nPluginLang::new);
        List<CompletableFuture<Pair<String, LangFile>>> futures = new ArrayList<>();

        for (String lang : supportedLanguages) {
            File targetFile = new File(projectLangFolder, lang + ".yml");

            futures.add(CompletableFuture.supplyAsync(() -> {
                LangFile merged = new LangFile(targetFile);

                merged.putAll(loadDefaultsFromPlugin(consumer, lang));
                merged.putAll(config.isFetchTranslationFiles() ? loadDefaultsFromAPI(consumer, lang) : null);
                merged.putAll(pluginLang.getLangFile(lang));

                merged.save();

                return new Pair<>(lang, merged);
            }, INSUtils.getInstance().getExecutor()));
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (CompletableFuture<Pair<String, LangFile>> f : futures) {
            Pair<String, LangFile> entry = f.join();
            if (entry == null) {
                continue;
            }

            String lang = entry.getK();
            LangFile fetched = entry.getV();
            pluginLang.addLang(lang, fetched);
        }
    }

    public static I18n getInstance() {
        return instance;
    }

    public static void init(INSUtils plugin) {
        instance = new I18n(plugin);
        instance.reload();
    }

    public I18nConfig getConfig() {
        return config;
    }
    public LangFile loadDefaultsFromPlugin(I18nConsumer plugin, String lang) {
        InputStream stream = plugin.getResource("lang/" + lang + ".yml");
        if (stream == null) return null;
        return new LangFile(stream);
    }

    public LangFile loadDefaultsFromAPI(I18nConsumer plugin, String lang) {
        return loadDefaultsFromAPI(plugin.getSlug(), lang);
    }

    public LangFile loadDefaultsFromAPI(String plugin, String lang) {
        HttpClient client = getHttpClient();

        JsonElement element;

        try {
            HttpResponse<InputStream> response = client.send(
                    HttpRequest
                            .newBuilder(
                                URI.create(API_ENDPOINT_HOST).resolve("/api/i18n/projects/" + plugin + "/translations?lang="+lang)
                            )
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            if (response.statusCode() != 200) return null;

            try (InputStream stream = response.body()) {
                element = JsonParser.parseReader(new InputStreamReader(stream));
            }
        } catch (IOException e) {
            logger.error("Error while trying to load default " + lang + " language" + e.getMessage());
            return null;
        } catch (InterruptedException e) {
            logger.error("Interrupted while trying to load default " + lang + " language" + e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }

        if (element == null) return null;

        File dataFolder = this.plugin.getDataFolder();
        File langFolder = new File(dataFolder, "lang");

        File langFile = new File(new File(langFolder, plugin), lang + ".yml");

        JsonObject root = element.getAsJsonObject();
        JsonObject translations = root.getAsJsonObject("translations");

        Map<String, LangEntry>  defaults = new HashMap<>();

        translations.entrySet().forEach(entry -> {
            JsonElement value = entry.getValue();
            if (value.isJsonArray())
                defaults
                        .put(
                                entry.getKey(),
                                new LangEntry(
                                        value
                                                .getAsJsonArray()
                                                .asList()
                                                .stream()
                                                .map(JsonElement::getAsJsonPrimitive)
                                                .map(JsonElement::getAsString)
                                                .toList()
                                )
                        );
            else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString())
                defaults
                        .put(
                                entry.getKey(),
                                new LangEntry(
                                        value
                                                .getAsJsonPrimitive()
                                                .getAsString()
                                )
                        );
        });

        return new LangFile(langFile, defaults);
    }

    public Set<String> getProjects() {
        HttpClient client = getHttpClient();
        try {

            HttpResponse<InputStream> response = client.send(
                    HttpRequest
                            .newBuilder(URI.create(API_ENDPOINT_HOST).resolve("/api/i18n/projects"))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );


            if (response.statusCode() != 200) return Set.of();

            JsonElement element;
            try (InputStream stream = response.body()) {
                element = JsonParser.parseReader(new InputStreamReader(stream));
            }

            JsonArray root = element.getAsJsonArray();

            return root
                    .asList()
                    .stream()
                    .map(JsonElement::getAsJsonObject)
                    .map(entry -> entry.get("slug").getAsString())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            logger.error("Error while getting projects", e);
            return Set.of();
        } catch (InterruptedException e) {
            logger.error("Interrupted while getting projects", e);
            Thread.currentThread().interrupt();
            return Set.of();
        }
    }

    public List<String> getLanguages() {
        HttpClient client = getHttpClient();

        HttpResponse<InputStream> response;

        try {
            response = client.send(
                    HttpRequest
                            .newBuilder(
                                    URI.create(API_ENDPOINT_HOST).resolve("/api/i18n/languages")
                            )
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            if (response.statusCode() != 200) return List.of();

            JsonElement element;
            try (InputStream stream = response.body()) {
                element = JsonParser.parseReader(new InputStreamReader(stream));
            }

            JsonArray root = element.getAsJsonArray();
            return root.asList().stream().map(JsonElement::getAsString).toList();
        } catch (IOException e) {
            logger.error("Error while getting languages", e);
            return List.of();
        } catch (InterruptedException e) {
            logger.error("Interrupted while getting languages", e);
            Thread.currentThread().interrupt();
            return List.of();
        }
    }
}
