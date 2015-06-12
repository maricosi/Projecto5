import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import pt.uc.dei.aor.paj.ImageType;
import pt.uc.dei.aor.paj.NoticiaType;


public class Crawler {
	public static void main(String[] args) throws IOException {

		Document doc = Jsoup.connect("http://edition.cnn.com").get();
		//String title = doc.title();

		List<String> categories = Arrays.asList(new String[]{"us", "europe", "asia", "africa", "china", "americas", "middleeast"}); 
		Elements noticias = doc.getElementsByClass("cd__headline");

		List<String> links = new ArrayList<>();

		for (Element n : noticias) {
			Element news = n.getElementsByTag("a").first();
			String link = news.attr("href");

			String[] fields = link.split("/");
			//System.out.println(Arrays.toString(fields));

			try {
				if (fields[1].substring(0,3).equals("201") && categories.indexOf(fields[4]) != -1 &&
						!fields[5].equals("gallery")){
					boolean contemNoticia=false;
					for(String l:links){
						if(l.equals(link)){
							contemNoticia=true;
						}
					}

					if(!contemNoticia){
						links.add(link);
					}
				}
			}
			catch(Exception e) {

			}
		}

		for (String l : links) {
			contextOfNews("http://edition.cnn.com" + l);
		}

	}


	public static void contextOfNews(String href) throws IOException{
		NoticiaType noticia= new NoticiaType();
		noticia.setUrl(href);
		System.out.println(href);
		// exemplo de teste
		Document doc = Jsoup.connect(href).get();
		
//		Elements video=doc.getElementsByTag("video");
//		if(video.size()>0){
//			System.out.println("tem video");
//		}
//		Elements flash=doc.getElementsByClass("js-el__video__play-button");
//		if (flash.size() > 0)
//			System.out.println("tem flash");

		Element title = doc.getElementsByClass("pg-headline").first();
		System.out.println(title.text());
		noticia.setTitulo(title.text());
		
		Element data = doc.getElementsByClass("update-time").first();
		System.out.println(data.text());
		//noticia.setDate(value);

		Element author = doc.getElementsByClass("metadata__byline__author").first();
		System.out.println(author.text());
		noticia.setAuthor(author.text());
		
		Elements highlights = doc.getElementsByClass("el__storyhighlights__item");
		
		for (Element e : highlights) {
			System.out.println(e.text());
			noticia.getHighlights().add(e.text());
		}

		Elements paragraphs = doc.getElementsByClass("zn-body__paragraph");
		String news="";
		for (Element e : paragraphs) {
			System.out.println(e.text());
			news+=e.text()+"\n";
		}
		noticia.setNewstext(news);


		System.out.println("full Images");
		Elements fullImages = doc.getElementsByClass("el__image--fullwidth");
		for (Element e : fullImages) {
			Element image = e.getElementsByTag("img").first();
			System.out.println(image.attr("data-src-large"));
			ImageType img = new ImageType();
			img.setUrl(image.attr("data-src-large"));
			
			Elements caption= e.getElementsByClass("media__caption");
			if(caption.size()>0){
				System.out.println("Captione-img: "+caption.first().text());
				img.setCaption(caption.first().text());
			}
			noticia.getImage().add(img);
		}

		System.out.println("expand images");
		Elements expandImages= doc.getElementsByClass("el__image--expandable");
		for(Element e: expandImages){
			Element image=e.getElementsByTag("img").first();
			System.out.println(image.attr("data-src-large"));
			ImageType img = new ImageType();
			img.setUrl(image.attr("data-src-large"));
			Elements caption= e.getElementsByClass("media__caption");
			if(caption.size()>0){
				System.out.println("Captione-img: "+caption.first().text());
				img.setCaption(caption.first().text());
			}
			noticia.getImage().add(img);
		}


		System.out.println("Video");
		Elements video = doc.getElementsByClass("js-media__video");
		for (Element e : video) {
			Element url = e.select("meta[itemprop=url]").first();
			System.out.println(url.attr("content"));
		}
	}
}
