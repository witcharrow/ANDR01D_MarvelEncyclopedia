/**
 * 
 */
package alex.mj.marvelencyclopedia;

/**
 * @author alejandro.marijuan@googlemail.com
 *
 */
public class CharacterInfo {
	
	private String id;
	private String name;
	private String description;
	private String modified;
	private String thumbnail_url;
	private String thumbnail_extension;
	
	public CharacterInfo(String id, String name, String description, String modified, String thumbnail_url, String thumbnail_extension){
		this.id = id;
		this.name = name;
		this.description = description;
		this.modified = modified;
		this.thumbnail_url = thumbnail_url;
		this.thumbnail_extension = thumbnail_extension;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the modified
	 */
	public String getModified() {
		return modified;
	}
	/**
	 * @param modified the modified to set
	 */
	public void setModified(String modified) {
		this.modified = modified;
	}
	/**
	 * @return the thumbnail_url
	 */
	public String getThumbnail_url() {
		return thumbnail_url;
	}
	/**
	 * @param thumbnail_url the thumbnail_url to set
	 */
	public void setThumbnail_url(String thumbnail_url) {
		this.thumbnail_url = thumbnail_url;
	}
	/**
	 * @return the thumbnail_extension
	 */
	public String getThumbnail_extension() {
		return thumbnail_extension;
	}
	/**
	 * @param thumbnail_extension the thumbnail_extension to set
	 */
	public void setThumbnail_extension(String thumbnail_extension) {
		this.thumbnail_extension = thumbnail_extension;
	}

}
