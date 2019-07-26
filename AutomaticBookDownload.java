package pack1;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public class AutomaticBookDownload {

	public static void main(String[] args)throws IOException {
		// TODO Auto-generated method stub

		HttpClient client = new HttpClient();
		CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
		GetMethod getMethod = null;
		BufferedOutputStream outputStream = null;
		PostMethod postMethod = null;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the title: ");
		String title = br.readLine();
		
		getMethod = new GetMethod("http://gen.lib.rus.ec/");
		client.executeMethod(getMethod);
		//creating prelogin book page file
		File prelogin = new File("D:\\Book_prelogin.html");
		prelogin.createNewFile();
		outputStream = new BufferedOutputStream(new FileOutputStream(prelogin));
		outputStream.write(getMethod.getResponseBodyAsString().getBytes());
		outputStream.close();
		
		String[] title_words = title.split(" ");
		String query = "";
		for(String s:title_words) 
			query = query + s +"+";
		query = query.substring(0,query.length() - 1);
		System.out.println(query);
		
		String url = "http://gen.lib.rus.ec/search.php?req="+query+"&lg_topic=libgen&open=0&view=simple&res=25&phrase=1&column=def";
		getMethod = new GetMethod(url);
		client.executeMethod(getMethod);
		//creating prelogin book page file
		File login_title = new File("D:\\Book_login.html");
		login_title.createNewFile();
		outputStream = new BufferedOutputStream(new FileOutputStream(login_title));
		outputStream.write(getMethod.getResponseBodyAsString().getBytes());
		
		Pattern pat = Pattern.compile("md5=([^\\']+)"); 
		Matcher mat = pat.matcher(getMethod.getResponseBodyAsString().toString());
		String md5 = null;
		while(mat.find()) {
			//can include as many as needed, I'm taking the first pdf
			md5 = mat.group().substring(4);
			System.out.println("Matcher: "+mat.group().substring(4));
			break;
		}
	
		outputStream.close();
	
		
		url = "http://93.174.95.29/_ads/"+md5;
		getMethod = new GetMethod(url);
		client.executeMethod(getMethod);
		System.out.println("Regex for link: "+"<h2 style=\"text-align:center\"><a href=\"([^\"]+)");
		Pattern pat1 = Pattern.compile("<h2 style=\"text-align:center\"><a href=\"([^\"]+)"); 
		Matcher mat1 = pat1.matcher(getMethod.getResponseBodyAsString().toString());
		
		String pdf_url = null;
		while(mat1.find()) {
			//can include as many as needed, I'm taking the first pdf
			pdf_url = mat1.group().substring(38);
			pdf_url = "http://93.174.95.29" + pdf_url.substring(1); 
			System.out.println("Matcher1: "+pdf_url);
			break;
		}
		
		
		File book = new File("D:\\"+title+".pdf");
		book.createNewFile();
		
		HttpGet h = new HttpGet(pdf_url);
		InputStream in = closeableHttpClient.execute(h).getEntity().getContent();
		FileOutputStream fileOutputStream = new FileOutputStream(book);
		
		System.out.println("Downloading file, please wait...");
		int j;
		while((j = in.read())!=-1)
			fileOutputStream.write(j);
		in.close();
		fileOutputStream.close();
		
		System.out.println("File downloaded");
		
	}

}
