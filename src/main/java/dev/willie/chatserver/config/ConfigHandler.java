package dev.willie.chatserver.config;

public class ConfigHandler{
	public static String SERVER_COMMUNICATION_TOKEN;
	public static String WEB_SERVER_ADDRESS;
	public static String APP_ID;
	public static String APP_SECRET;
	public static String LOGTO_OIDC_TOKEN_ENDPOINT;
	public static String LOGTO_API_ENDPOINT;
	public static void loadConfigs(){
		SERVER_COMMUNICATION_TOKEN = System.getenv("SERVER_COMMUNICATION_TOKEN");
		WEB_SERVER_ADDRESS = System.getenv("WEB_SERVER_ADDRESS");
		APP_ID = System.getenv("APP_ID");
		APP_SECRET = System.getenv("APP_SECRET");
		LOGTO_OIDC_TOKEN_ENDPOINT = System.getenv("LOGTO_OIDC_TOKEN_ENDPOINT");
		LOGTO_API_ENDPOINT = System.getenv("LOGTO_API_ENDPOINT");
	}
}
