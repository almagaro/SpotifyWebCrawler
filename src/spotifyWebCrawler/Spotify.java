package spotifyWebCrawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

/**
 * Tool to get the top 200 Canadian weekly songs from Spotify Charts and get each track 
 * feature using Spotify APIs, and save data to a CSV file
 * 
 * @author Alda R.
 *
 */
public class Spotify 
{
	private static final String DATE_FORMAT1 = "MM/dd/yyyy";
	private static final String DATE_FORMAT2 = "yyyy-MM-dd";

	private final static String SPOTIFY_DOWNLOAD_URL = "https://spotifycharts.com/regional/ca/weekly/%s--%s/download";
	private final static String SPOTIFY_URL = "https://spotifycharts.com/regional/ca/weekly/%s--%s";
	private final static String USER_STORAGE_LOCATION = "C:\\"; //TODO: Change user storage location
	
	private final static String AUDIO_FEATURES_SPOTIFY_API = "https://api.spotify.com/v1/audio-features/";
	private final static String TRACK_FEATURES_SPOTIFY_API = "https://api.spotify.com/v1/tracks/";
	private final static String TOKEN = "??????"; //TODO: get token
	
	private final static String FINAL_FILE_NAME = "SpotifyTracksTest.csv";

	public static void getFiles(String fromDateString, String toDateString) throws Exception 
	{
		Date startWeekDate = new SimpleDateFormat(DATE_FORMAT1).parse(fromDateString);
		Date endWeekDate = new SimpleDateFormat(DATE_FORMAT1).parse(toDateString);

		List<SpotifyTracksData> tracks = new ArrayList<SpotifyTracksData>();
		HashMap<String, SpotifyTracksData> trackApiInfo = new HashMap<String, SpotifyTracksData>();

		Date fromDate = startWeekDate;
		Date toDate = DateUtils.addDays(startWeekDate, 7);
		int weekCounter = 1;

		//Loop to get weekly list of songs
		while (toDate.before(endWeekDate)) 
		{
			  List<SpotifyTracksData> weeklyTracksFromWebsite = getWeeklyTracksList(fromDate, toDate, weekCounter);
				//Loop to get the 200 weekly tracks
				for (SpotifyTracksData track : weeklyTracksFromWebsite) 
				{
					String trackId = track.getUrl().substring(31);
					track.setWeekStartDate(fromDate);
					track.setWeekEndDate(toDate);
					track.setWeekNumber(weekCounter);
					track.setTrackId(trackId);
					trackApiInfo.put(trackId, new SpotifyTracksData());
					tracks.add(track);
				}

			// Increment fromDate & toDate & weekCounter
			fromDate = DateUtils.addDays(fromDate, 7);
			toDate = DateUtils.addDays(toDate, 7);
			++weekCounter;
		}
		
		System.out.println("Number of files: " + tracks.size());
		System.out.println("Number of tracks: " + trackApiInfo.size());
		
		//Call the 2 APIs to get track info
		trackApiInfo = getTrackApiInfo(trackApiInfo);
		
		//Merge tracksInfo & trackIds
		tracks = mergeTracksInfo(tracks, trackApiInfo);
		
		//Insert list of objects to new CSV file
		createCSVFile(tracks);
	}
	
	
	/**
	 * Use Jsoup to crawl Spotify Charts website and get all top 200 songs and 
	 * their information: url, position #, song name, artist and # of streams of that week
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param weekCounter
	 * @return List<SpotifyTracksData>
	 */
	private static List<SpotifyTracksData> getWeeklyTracksList(Date fromDate, Date toDate, int weekCounter) 
	{
		// Use fromDate & toDate to download
		String spotifyURL = String.format(SPOTIFY_URL, dateToString(fromDate), dateToString(toDate));
		System.out.println("(" + weekCounter + ")"+spotifyURL);
		List<SpotifyTracksData> weeklyTracks = new ArrayList<SpotifyTracksData>();
		try 
		{
			Document document = Jsoup.connect(spotifyURL).get();
			Element table = document.getElementsByClass("chart-table").first();
			Elements trs = table.select("tbody>tr");
			
			for(Element tr : trs)
			{
			    //System.out.println(tr.text());
			    String trackURL = tr.getElementsByClass("chart-table-image").first().select("a").attr("href");
				int trackPosition = Integer.valueOf(tr.getElementsByClass("chart-table-position").first().text());
				String trackName = tr.select("td.chart-table-track strong").first().text().replace("by", "").trim();
				String trackArtist = tr.select("td.chart-table-track span").first().text().replace("by", "").trim();
				int trackStrems = Integer.valueOf(tr.getElementsByClass("chart-table-streams").first().text().replace(",", "").trim());
				
				weeklyTracks.add(new SpotifyTracksData(trackURL, trackPosition, trackName, trackArtist, trackStrems));
			}
		} 
		catch (Exception e) 
		{
			System.out.println("(" + weekCounter + ") Error dowloading file for dates: " + fromDate + "-" + toDate);
		}
        return weeklyTracks;
	}

	private static void createCSVFile(List<SpotifyTracksData> tracks) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException 
	{
		Writer writer = Files.newBufferedWriter(Paths.get(USER_STORAGE_LOCATION, FINAL_FILE_NAME));
		StatefulBeanToCsvBuilder<SpotifyTracksData> builder = new StatefulBeanToCsvBuilder<>(writer);
	    StatefulBeanToCsv<SpotifyTracksData> beanWriter = builder.build();
	    beanWriter.write(tracks);
	    writer.close();
	}

	
	
	/**
	 * Update tracks list with data retrieved from APIs
	 * 
	 * @param tracks
	 * @param tracksAPIsInfo
	 * @return List<SpotifyTracksData>
	 */
	private static List<SpotifyTracksData> mergeTracksInfo(List<SpotifyTracksData> tracks,
			HashMap<String, SpotifyTracksData> tracksAPIsInfo) 
	{
		for(SpotifyTracksData track : tracks)
		{
			SpotifyTracksData trackApiInfo = tracksAPIsInfo.get(track.getTrackId());
			track.setTrackValence(trackApiInfo.getTrackValence());
			track.setTrackEnergyLevel(trackApiInfo.getTrackEnergyLevel());
			track.setTrackDanceability(trackApiInfo.getTrackDanceability());
			track.setTrackPopularity(trackApiInfo.getTrackPopularity());
			track.setTrackReleaseDate(trackApiInfo.getTrackReleaseDate());
			
			//Handle different date formats (yyyy, yyyy-mm, mm/dd/yyyy)
			String releaseYear = trackApiInfo.getTrackReleaseDate();
			releaseYear = releaseYear.length() > 4 ? releaseYear.substring(0, 4) : releaseYear;
			track.setTrackReleaseYear(releaseYear);
		}
		
		return tracks;
	}

	/**
	 * Format date to string: "yyyy-MM-dd"
	 * 
	 * @param date
	 * @return String
	 */
	public static String dateToString(Date date) 
	{
		return new SimpleDateFormat(DATE_FORMAT2).format(date);
	}
	
	
	/**
	 * Get each track feature (valence, energy, danceability) from "/v1/audio-features/" API 
	 * and get release date and popularity from "/v1/tracks/" API
	 * 
	 * @param trackIds
	 * @return HashMap<String, SpotifyTracksData> - key trackID and value object with values retrieved from APIs
	 */
	private static HashMap<String, SpotifyTracksData> getTrackApiInfo(HashMap<String, SpotifyTracksData> trackIds) 
	{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		int counter = 0;
					 
		for (Entry<String, SpotifyTracksData> track : trackIds.entrySet()) 
		{
			try 
			{ 
				System.out.println("Key: "+track.getKey() + " & Value: " + track.getValue() + " Counter: " + counter);
			    ++counter;
			       
				// Call first API to get track AUDIO features
				HttpGet request = new HttpGet(AUDIO_FEATURES_SPOTIFY_API + track.getKey());
				CloseableHttpResponse response = executeRequest(request, httpClient);
				HttpEntity entity = response.getEntity();
				if (entity != null) 
				{
					String result = EntityUtils.toString(entity);
					JsonObject jsonObject = new Gson().fromJson(result, JsonObject.class);
					System.out.println("jsonObject "+jsonObject);
						
					track.getValue().setTrackValence(Float.parseFloat(jsonObject.get("valence").getAsString()));
					track.getValue().setTrackEnergyLevel(Float.parseFloat(jsonObject.get("energy").getAsString()));
					track.getValue().setTrackDanceability(Float.parseFloat(jsonObject.get("danceability").getAsString()));
				}
	
				// Call second API to get track features
				HttpGet request2 = new HttpGet(TRACK_FEATURES_SPOTIFY_API + track.getKey());
				CloseableHttpResponse response2 = executeRequest(request2, httpClient);
				HttpEntity entity2 = response2.getEntity();
				if (entity2 != null) 
				{
					String result2 = EntityUtils.toString(entity2);
					JsonObject jsonObject = new Gson().fromJson(result2, JsonObject.class);
					System.out.println("jsonObject: "+ jsonObject);
					
					track.getValue().setTrackReleaseDate(jsonObject.get("album").getAsJsonObject().get("release_date").getAsString());
					track.getValue().setTrackPopularity(Float.parseFloat(jsonObject.get("popularity").getAsString()));
				}
			}
			catch (Exception e) 
			{
				System.out.println("Error getting API data:  "+ e.getMessage());
				e.printStackTrace();
			}
		  }
		return trackIds;
	}

	/**
	 * @param request
	 * @param httpClient
	 * @return CloseableHttpResponse
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static CloseableHttpResponse executeRequest(HttpGet request, CloseableHttpClient httpClient)
			throws ClientProtocolException, IOException, InterruptedException 
	{
		request.addHeader("custom-key", "mkyong");
		request.addHeader(HttpHeaders.ACCEPT, "application/json");
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN);
		CloseableHttpResponse response = httpClient.execute(request);
		
		//Check for retry-after if is not empty then wait and execute the request again
		Header[] headers = response.getHeaders("retry-after");
		if(headers.length > 0 )
		{
			int retryAfter = Integer.valueOf(headers[0].getValue());
			System.out.println("retry-after: " + retryAfter + "s");
			Thread.sleep((retryAfter+3)*1000);
			request.releaseConnection();
			response = httpClient.execute(request);
		}
		return response;
	}
	
	
	/**
	 * Method to download CSV files from Spotify charts, but currently is not used because there are some 
	 * files missing on their website
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param weekCounter
	 * @return List<SpotifyTracksData>
	 * @throws Exception
	 */
	private static List<SpotifyTracksData> getWeeklyTracksListFile(Date fromDate, Date toDate, int weekCounter) throws Exception
	{
		// Use fromDate & toDate to download
		String downloadURL = String.format(SPOTIFY_DOWNLOAD_URL, dateToString(fromDate), dateToString(toDate));
		System.out.println(downloadURL);
		
		//Save file
		byte[] fileBytes = downloadFileFromSpotify(downloadURL);
		File newFile = saveFileToStorage(fileBytes, "Week#" + weekCounter + ".csv");
        
		//Read file: CSV to List<SpotifyTracksData>
		BufferedReader br = Files.newBufferedReader(newFile.toPath(), StandardCharsets.UTF_8);
        HeaderColumnNameMappingStrategy<SpotifyTracksData> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(SpotifyTracksData.class);
        
        CsvToBean<SpotifyTracksData> csvToBean = new CsvToBeanBuilder<SpotifyTracksData>(br)
        		.withSkipLines(1)
        		.withMappingStrategy(strategy)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        
        return csvToBean.parse();
        
	}
	
	/**
	 * Save file to and specific location 
	 * 
	 * @param fileBytes
	 * @param fileName
	 * @return File
	 * @throws Exception
	 */
	private static File saveFileToStorage(byte[] fileBytes, String fileName) throws Exception 
	{
		try 
		{
			File newFile = new File(USER_STORAGE_LOCATION, fileName);
			newFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(newFile);
			outputStream.write(fileBytes);
			outputStream.close();
			return newFile;
		} catch (IOException e) 
		{
			throw new Exception("Error saving file into local storage");
		}

	}
	

	/**
	 * Used to download files using Jsoup
	 * 
	 * @param url
	 * @return byte[]
	 * @throws Exception
	 */
	private static byte[] downloadFileFromSpotify(String url) throws Exception 
	{
		try {
			byte[] bytes = Jsoup.connect(url).header("Accept-Encoding", "csv, deflate").referrer(url)
					.ignoreContentType(true).maxBodySize(0).timeout(600000).execute().bodyAsBytes();
			return bytes;
		} catch (IOException e) {
			throw new Exception("Error downloading file from Spotify website");
		}
	}

}
