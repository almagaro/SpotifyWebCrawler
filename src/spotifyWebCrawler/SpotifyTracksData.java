package spotifyWebCrawler;

import java.util.Date;

import com.opencsv.bean.CsvBindByName;

public class SpotifyTracksData {

	@CsvBindByName(column = "Position")
	private int position;
	
	@CsvBindByName(column = "Track Name")
	private String trackName;
	
	@CsvBindByName(column = "Artist")
	private String artist;
	
	@CsvBindByName(column = "Streams")
	private int streams;
	
	@CsvBindByName(column = "URL")
	private String url;
	
	@CsvBindByName(column = "Track ID")
	private String trackId;
	
	@CsvBindByName(column = "Week Number")
	private int weekNumber;
	
	@CsvBindByName(column = "Week Start Date")
	private Date weekStartDate;
	
	@CsvBindByName(column = "Week End Date")
	private Date weekEndDate;
	
	@CsvBindByName(column = "Track Release Date")
	private String trackReleaseDate;
	
	@CsvBindByName(column = "Track Release Year")
	private String trackReleaseYear;
	
	@CsvBindByName(column = "Track Valence")
	private float trackValence;
	
	@CsvBindByName(column = "Track Energy Level")
	private float trackEnergyLevel;
	
	@CsvBindByName(column = "Track Danceability")
	private float trackDanceability;
	
	@CsvBindByName(column = "Track Popularity")
	private float trackPopularity;
	
	
	public SpotifyTracksData(String trackURL, int trackPosition, String trackName, String trackArtist,
			int trackStrems) 
	{
		this.url = trackURL;
		this.position = trackPosition;
		this.trackName = trackName;
		this.artist = trackArtist;
		this.streams = trackStrems;
	}
	
	public SpotifyTracksData() 
	{
	}

	
	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the trackName
	 */
	public String getTrackName() {
		return trackName;
	}

	/**
	 * @param trackName the trackName to set
	 */
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	/**
	 * @return the artist
	 */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * @param artist the artist to set
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}

	/**
	 * @return the streams
	 */
	public int getStreams() {
		return streams;
	}

	/**
	 * @param streams the streams to set
	 */
	public void setStreams(int streams) {
		this.streams = streams;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the trackId
	 */
	public String getTrackId() {
		return trackId;
	}

	/**
	 * @param trackId the trackId to set
	 */
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

	/**
	 * @return the weekNumber
	 */
	public int getWeekNumber() {
		return weekNumber;
	}
	

	/**
	 * @param weekNumber the weekNumber to set
	 */
	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}

	/**
	 * @return the weekStartDate
	 */
	public Date getWeekStartDate() {
		return weekStartDate;
	}

	/**
	 * @param weekStartDate the weekStartDate to set
	 */
	public void setWeekStartDate(Date weekStartDate) {
		this.weekStartDate = weekStartDate;
	}

	/**
	 * @return the weekEndDate
	 */
	public Date getWeekEndDate() {
		return weekEndDate;
	}

	/**
	 * @param weekEndDate the weekEndDate to set
	 */
	public void setWeekEndDate(Date weekEndDate) {
		this.weekEndDate = weekEndDate;
	}

	/**
	 * @return the trackReleaseDate
	 */
	public String getTrackReleaseDate() {
		return trackReleaseDate;
	}

	/**
	 * @param trackReleaseDate the trackReleaseDate to set
	 */
	public void setTrackReleaseDate(String trackReleaseDate) {
		this.trackReleaseDate = trackReleaseDate;
	}

	/**
	 * @return the trackReleaseYear
	 */
	public String getTrackReleaseYear() {
		return trackReleaseYear;
	}

	/**
	 * @param trackReleaseYear the trackReleaseYear to set
	 */
	public void setTrackReleaseYear(String trackReleaseYear) {
		this.trackReleaseYear = trackReleaseYear;
	}

	/**
	 * @return the trackValence
	 */
	public float getTrackValence() {
		return trackValence;
	}

	/**
	 * @param trackValence the trackValence to set
	 */
	public void setTrackValence(float trackValence) {
		this.trackValence = trackValence;
	}

	/**
	 * @return the trackEnergyLevel
	 */
	public float getTrackEnergyLevel() {
		return trackEnergyLevel;
	}

	/**
	 * @param trackEnergyLevel the trackEnergyLevel to set
	 */
	public void setTrackEnergyLevel(float trackEnergyLevel) {
		this.trackEnergyLevel = trackEnergyLevel;
	}

	/**
	 * @return the trackDanceability
	 */
	public float getTrackDanceability() {
		return trackDanceability;
	}

	/**
	 * @param trackDanceability the trackDanceability to set
	 */
	public void setTrackDanceability(float trackDanceability) {
		this.trackDanceability = trackDanceability;
	}

	/**
	 * @return the trackPopularity
	 */
	public float getTrackPopularity() {
		return trackPopularity;
	}

	/**
	 * @param trackPopularity the trackPopularity to set
	 */
	public void setTrackPopularity(float trackPopularity) {
		this.trackPopularity = trackPopularity;
	}
	
}