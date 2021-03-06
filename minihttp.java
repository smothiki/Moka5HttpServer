import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.sun.net.httpserver.*;

class Server {
	private int port;
	private String root;

	public Server(String configfile) throws IOException {
		BufferedReader config = new BufferedReader(new FileReader(configfile));
		String line = null;

		while ((line = config.readLine()) != null) {
			String[] parts = line.split("=");
			String key = parts[0], value = parts[1];
			if (key.equals("port"))
				port = Integer.parseInt(value);
			else if (key.equals("root"))
				root = value;
		}
	}

	public void serve() throws IOException {
		HttpServer hs = HttpServer.create(new InetSocketAddress(port), 5);
		hs.createContext("/", new RequestHandler(root));
		hs.start();
	}
}

class RequestHandler implements HttpHandler {
	String root;
	List list=new List();
	
	public RequestHandler(String root) {
		this.root = root;
	}
	
	/*Function to Print contents in a Directory 
	 * Checks if the path is root or Not and gives the contents according to the path 
	 */
	
	public void printContents(HttpExchange t,String path) throws IOException
	{
		
		StringBuilder body = new StringBuilder();
		String directory;
		body.append("<table border=\"1\">");
		body.append("<tr>");
		body.append("<td>"+"<h5>Index</h5>"+"</td>");
		body.append("<td>"+"<h5>Name</h5>"+"</td>");
		body.append("</tr>");
		
		//Example localhost:55555/
		if(path.indexOf('/')==-1)
		{
			directory="/";
		}
		else
			//Example localhost:55555/bootstrap then directory is bootstrap/
			directory=path.substring(path.indexOf('/'))+"/";
		
		//ArrayList of files and Directories 
		ArrayList<ArrayList<String> > fil_dir=list.listNames(path);
		int counter=1;
		for(ArrayList<String> Str:fil_dir){
			for(String str:Str)
			{	
				body.append("<tr>");
				body.append("<td>"+counter+"</td>");
				body.append("<td>"+"<a href="+directory+str+">"+str+"</a><br/>"+"</td>");
				body.append("</tr>");
				counter++;
			}
		}
		body.append("</table>");
		
		t.getResponseHeaders().set("Content-type", "text/html");
		t.sendResponseHeaders(200, body.length());
		OutputStream os = t.getResponseBody();
		os.write(body.toString().getBytes());
		os.close();
	}
	
	public void handle(HttpExchange t) throws IOException {
		if (!t.getRequestMethod().equals("GET")) {
			sendError(t, 405, "Method not allowed");
			return;
		}
		
		File path = getPath(t);
		
		/*Checking whether the path is file or directory
		 * if it is directory then calls print contents 
		 * else if it is file proceeds normal execution 
		 */
		if(!(list.isFile(path.getPath()))&&(list.isDir(path.getPath())))
		{
			printContents(t,path.getPath());
			return;
		}
		
		FileInputStream f;
		System.err.println(path.getPath());
		try {
			f = new FileInputStream(path);
		} catch (IOException e) {
			sendError(t, 404, "File not found");
			return;
		}

		Headers headers = t.getResponseHeaders();
		String ctype = lookupContentType(path.getPath());
		headers.set("Content-type", ctype);
		headers.set("Content-length", Long.toString(path.length()));
		t.sendResponseHeaders(200, path.length());

		OutputStream os = t.getResponseBody();
		byte[] buf = new byte[1024];
		int n;
		while ((n = f.read(buf, 0, 1024)) > 0) {
			os.write(buf, 0, n);
		}
		os.close();
	}

	private static void sendError(HttpExchange t, int error, String description) throws IOException {
		String body = "<h1>" + Integer.toString(error) + " " + description + "</h1>";
		t.getResponseHeaders().set("Content-type", "text/html");
		t.sendResponseHeaders(error, body.length());
		OutputStream os = t.getResponseBody();
		os.write(body.getBytes());
		os.close();
	}

	private File getPath(HttpExchange t) throws UnsupportedEncodingException {
		String path = URLDecoder.decode(t.getRequestURI().getPath(), "UTF-8");
		path = path.replace('/', File.separatorChar).substring(1);
		
		
		String Path=null;
		if(path.length()!=0)
			Path=root+"/"+path;
		else
			Path=root;
		
		/*Checks whether a particular folder has index.html 
		 * If a folder has index.html appends index.html at the end of the path 
		 */
		if(!list.isFile(Path)&&list.isDir(Path))
		{
			if(list.lookforIndex(Path))
			{
				path=path+"/index.html";
			}
		}
		
		File file=new File(root,path);
		return file;
	}


	//Added javascript and jpg mime types
	private static String lookupContentType(String path) {
		if (path.endsWith(".css"))
			return "text/css";
		else if (path.endsWith(".png"))
			return "image/png";
		else if (path.endsWith(".html"))
			return "text/html";
		else if (path.endsWith(".js"))
			return "application/javascript";
		else if (path.endsWith(".jpg"))
			return "image/jpeg";
		else
			return "text/plain";
	}
}

public class minihttp {
	public static void main(String[] args) throws IOException {
		Server server = new Server("config.txt");
		server.serve();
	}
}
