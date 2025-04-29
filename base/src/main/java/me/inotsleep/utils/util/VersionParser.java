package me.inotsleep.utils.util;

public class VersionParser {
    public static int stringToDataVersion(String version) {
        switch (version) {
            case "1.9" : return 169;
            case "1.9.1": return 175;
            case "1.9.2": return 176;
            case "1.9.3": return 183;
            case "1.9.4": return 184;
            case "1.10" : return 510;
            case "1.10.1": return 511;
            case "1.10.2": return 512;
            case "1.11" : return 819;
            case "1.11.1": return 921;
            case "1.11.2": return 922;
            case "1.12" : return 1139;
            case "1.12.1": return 1241;
            case "1.12.2": return 1343;
            case "1.13" : return 1519;
            case "1.13.1": return 1628;
            case "1.13.2": return 1631;
            case "1.14" : return 1952;
            case "1.14.1": return 1957;
            case "1.14.2": return 1963;
            case "1.14.3": return 1968;
            case "1.14.4": return 1976;
            case "1.15" : return 2225;
            case "1.15.1": return 2227;
            case "1.15.2": return 2230;
            case "1.16" : return 2566;
            case "1.16.1": return 2567;
            case "1.16.2": return 2578;
            case "1.16.3": return 2580;
            case "1.16.4": return 2584;
            case "1.16.5": return 2586;
            case "1.17" : return 2724;
            case "1.17.1": return 2730;
            case "1.18" : return 2860;
            case "1.18.1": return 2865;
            case "1.18.2": return 2975;
            case "1.19" : return 3105;
            case "1.19.1": return 3117;
            case "1.19.2": return 3120;
            case "1.19.3": return 3218;
            case "1.19.4": return 3337;
            case "1.20" : return 3463;
            case "1.20.1": return 3465;
            case "1.20.2": return 3578;
            case "1.20.3": return 3698;
            case "1.20.4": return 3700;
            case "1.20.5": return 3837;
            case "1.20.6": return 3839;
            case "1.21" : return 3953;
            case "1.21.1": return 3955;
            case "1.21.2": return 4080;
            case "1.21.3": return 4082;
            case "1.21.4": return 4189;
            default: return 0;
        }
    }

    public static String dataVersionToString(int version) {
        switch (version) {
            case 169: return "1.9";
            case 175: return "1.9.1";
            case 176: return "1.9.2";
            case 183: return "1.9.3";
            case 184: return "1.9.4";
            case 510: return "1.10";
            case 511: return "1.10.1";
            case 512: return "1.10.2";
            case 819: return "1.11";
            case 921: return "1.11.1";
            case 922: return "1.11.2";
            case 1139: return "1.12";
            case 1241: return "1.12.1";
            case 1343: return "1.12.2";
            case 1519: return "1.13";
            case 1628: return "1.13.1";
            case 1631: return "1.13.2";
            case 1952: return "1.14";
            case 1957: return "1.14.1";
            case 1963: return "1.14.2";
            case 1968: return "1.14.3";
            case 1976: return "1.14.4";
            case 2225: return "1.15";
            case 2227: return "1.15.1";
            case 2230: return "1.15.2";
            case 2566: return "1.16";
            case 2567: return "1.16.1";
            case 2578: return "1.16.2";
            case 2580: return "1.16.3";
            case 2584: return "1.16.4";
            case 2586: return "1.16.5";
            case 2724: return "1.17";
            case 2730: return "1.17.1";
            case 2860: return "1.18";
            case 2865: return "1.18.1";
            case 2975: return "1.18.2";
            case 3105: return "1.19";
            case 3117: return "1.19.1";
            case 3120: return "1.19.2";
            case 3218: return "1.19.3";
            case 3337: return "1.19.4";
            case 3463: return "1.20";
            case 3465: return "1.20.1";
            case 3578: return "1.20.2";
            case 3698: return "1.20.3";
            case 3700: return "1.20.4";
            case 3837: return "1.20.5";
            case 3839: return "1.20.6";
            case 3953: return "1.21";
            case 3955: return "1.21.1";
            case 4080: return "1.21.2";
            case 4082: return "1.21.3";
            case 4189: return "1.21.4";
            default: return "null";
        }
    }
}
