package com.light.network.proxy.driver;

import com.light.network.proxy.ProxyServer;


@interface ClassPreamble {
	   String author();
	   String date();
	   int currentRevision() default 1;
	   String lastModified() default "N/A";
	   String lastModifiedBy() default "N/A";
	   String[] reviewers();  // Note use of array
}

@ClassPreamble (
		author = "John Doe",
		date = "3/17/2002",
		currentRevision = 6,
		lastModified = "4/12/2004",
		lastModifiedBy = "Jane Doe",
		reviewers = {"Alice", "Bob", "Cindy"}
)
public class ProxyServerDriver {
	
	public static void main(final String [] args) {
		
		System.out.println("Running");
		final ProxyServer.MainServerThread server = ProxyServer.createProxyServer();
		server.initServerSocket();
		new Thread(server).start();
		server.mainServerLoop();
		System.out.println("Done");		
	}
	
} // End of the class
