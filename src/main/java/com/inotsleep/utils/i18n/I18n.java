package com.inotsleep.utils.i18n;

import com.google.gson.*;
import com.inotsleep.utils.logging.Logger;
import com.inotsleep.utils.plugin.BukkitPlugin;
import com.inotsleep.utils.plugin.BungeePlugin;
import com.inotsleep.utils.i18n.config.LangEntry;
import com.inotsleep.utils.i18n.config.LangFile;
import com.inotsleep.utils.logging.LoggingManager;
import com.inotsleep.utils.INSUtils;
import org.jetbrains.annotations.NotNull;

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
import java.util.concurrent.atomic.AtomicInteger;

public class I18n {
    private static final String API_ENDPOINT_HOST = "https://inotsleep.com/";
    private static I18n instance;

            private final ExecutorService executor = new ThreadPoolExecutor(
            2,
            16,
            5L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(127),
            new ThreadFactory() {
                private final AtomicInteger i = new AtomicInteger(1);

                @Override
                public Thread newThread(@NotNull Runnable r) {
                    Thread t = new Thread(r, "INSUtilsApi-I18n-executor-" + i.getAndIncrement());
                    t.setDaemon(true);
                    t.setUncaughtExceptionHandler((th, ex) ->
                            logger.error("Uncaught exception: ", ex)
                    );
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );


    private final INSUtils plugin;
    private final Logger logger;

    private HttpClient httpClient;
    private I18nConfig config;

    private Map<String, I18nPluginLang> pluginLangMap;
    private final Map<String, I18nConsumer> consumers = new HashMap<>();
    private List<String> remoteProjects;
    private List<String> supportedLanguages;

    private I18n(INSUtils plugin) {
        this.plugin = plugin;
        logger = LoggingManager.getPrefixedLogger("[I18n] ");
    }

    public void shutdown() {
        executor.shutdown();
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
    }

    public LangEntry getEntry(String key, String lang, String plugin) {
        I18nPluginLang pluginLang = pluginLangMap.get(plugin);
        if (pluginLang == null) return null;

        return pluginLang.getLangEntry(key, lang);
    }

    public String getString(String key, String lang, String plugin) {
        LangEntry entry = getEntry(key, lang, plugin);
        if (entry == null || entry.isList()) {
            if (entry == null) logger.debug("No entry found for key: " + key + ", lang: " + lang + ", plugin: " + plugin);
            else logger.debug("Entry for key: " + key + ", lang: " + lang + ", plugin: " + plugin + " is not String");

            return key;
        }

        return entry.getValue();
    }

    public List<String> getStringList(String key, String lang, String plugin) {
        LangEntry entry = getEntry(key, lang, plugin);
        if (entry == null || entry.isString()) {
            if (entry == null) logger.debug("No entry found for key: " + key + ", lang: " + lang + ", plugin: " + plugin);
            else logger.debug("Entry for key: " + key + ", lang: " + lang + ", plugin: " + plugin + " is not List");

            return List.of(key);
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
        Map<String, I18nPluginLang> newPluginLangMap = new HashMap<>();
        config = new I18nConfig();

        config.reload();

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        File langFolder = new File(dataFolder, "lang");
        if (!langFolder.exists()) langFolder.mkdirs();

        File[] files = langFolder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (!file.isDirectory()) continue;

            String pluginName = file.getName();
            I18nPluginLang pluginLang = new I18nPluginLang(pluginName);
            newPluginLangMap.put(pluginName, pluginLang);

            File[] langFiles = file.listFiles();
            if (langFiles == null) continue;

            for (File langFile : langFiles) {
                if (!langFile.isFile()) continue;
                if (!langFile.getName().endsWith(".yml")) continue;

                LangFile langFileObject = new LangFile(langFile);

                pluginLang.addLang(langFile.getName().replace(".yml", ""), langFileObject);
            }
        }

        pluginLangMap = newPluginLangMap;

        CompletableFuture<List<String>> remoteProjectsFuture = CompletableFuture.supplyAsync(
                this::getProjects,
                executor
        );
        CompletableFuture<List<String>> supportedLanguagesFuture = CompletableFuture.supplyAsync(
                this::getLanguages,
                executor
        );

        CompletableFuture.allOf(remoteProjectsFuture, supportedLanguagesFuture).join();

        remoteProjects = remoteProjectsFuture.resultNow();
        logger.info("Loaded " + remoteProjects.size() + " remote projects");
        supportedLanguages = supportedLanguagesFuture.resultNow();
        logger.info("Supporting " + supportedLanguages.size() + " language(s)");
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
    public LangFile loadDefaultsFromPlugin(BukkitPlugin plugin, String lang) {
        InputStream stream = plugin.getResource("lang/" + lang + ".yml");
        if (stream == null) return null;
        return new LangFile(stream);
    }

    public LangFile loadDefaultsFromPlugin(BungeePlugin plugin, String lang) {
        InputStream stream = plugin.getResourceAsStream("lang/" + lang + ".yml");
        if (stream == null) return null;
        return new LangFile(stream);
    }

    public LangFile loadDefaultsFromAPI(BukkitPlugin plugin, String lang) {
        return loadDefaultsFromAPI(plugin.getName().toLowerCase(), lang);
    }

    public LangFile loadDefaultsFromAPI(BungeePlugin plugin, String lang) {
        return loadDefaultsFromAPI(plugin.getDescription().getName().toLowerCase(), lang);
    }

    public LangFile loadDefaultsFromAPI(String plugin, String lang) {
        HttpClient client = getHttpClient();

        JsonElement element = null;

        try {
            HttpResponse<InputStream> response = client.send(
                    HttpRequest
                            .newBuilder(
                                URI.create(API_ENDPOINT_HOST).resolve("/api/i18n/projects/" + plugin + "/translations")
                            )
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );

            if (response.statusCode() != 200) return null;

            InputStream stream = response.body();

            element = JsonParser.parseReader(new InputStreamReader(stream));
        } catch (IOException | InterruptedException e) {
            logger.error("Error while trying to load default " + lang + " language" + e.getMessage());
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

        client.close();

        return new LangFile(langFile, defaults);
    }

    public List<String> getProjects() {
        HttpClient client = getHttpClient();

        HttpResponse<InputStream> response = null;
        try {
            response = client.send(
                    HttpRequest
                            .newBuilder(URI.create(API_ENDPOINT_HOST).resolve("/api/i18n/projects"))
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofInputStream()
            );
        } catch (IOException | InterruptedException e) {
            logger.error("Error while getting projects", e);
            return List.of();
        }

        if (response.statusCode() != 200) return List.of();

        InputStream stream = response.body();
        JsonElement element = JsonParser.parseReader(new InputStreamReader(stream));
        JsonArray root = element.getAsJsonArray();

        return root
                .asList()
                .stream()
                .map(JsonElement::getAsJsonObject)
                .map(entry -> entry.get("slug").getAsString())
                .toList();
    }

    public List<String> getLanguages() {
        HttpClient client = getHttpClient();

        HttpResponse<InputStream> response = null;

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
        } catch (IOException | InterruptedException e) {
            logger.error("Error while getting languages", e);
            return List.of();
        }

        if (response.statusCode() != 200) return List.of();

        InputStream stream = response.body();
        JsonElement element = JsonParser.parseReader(new InputStreamReader(stream));
        JsonArray root = element.getAsJsonArray();
        return root.asList().stream().map(JsonElement::getAsString).toList();
    }
}
