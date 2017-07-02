/**
 * Copyright (c) 2010-2016 Mark Allen, Norbert Bartels.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.restfb.example;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Time;

import com.restfb.*;
import com.restfb.example.Example;
import com.restfb.json.JsonArray;
import com.restfb.json.JsonObject;
import com.restfb.types.Comment;
import com.restfb.types.Page;
import com.restfb.types.Post;
import com.restfb.types.Url;
import com.restfb.types.User;

import java.util.*;

/**
 * Examples of RestFB's Graph API functionality.
 * 
 * @author <a href="http://restfb.com">Mark Allen</a>
 */
@SuppressWarnings("deprecation")
public class GraphReaderExample extends Example {
	/**
	 * O RestFB Graph API client.
	 */
	static String filename = "tmp.txt";
	static File fileut;
	static FileWriter fw;
	private final FacebookClient facebookClient23;
	// private final FacebookClient facebookClient20;O

	/**
	 * Entry point. You must provide a single argument on the command line: a
	 * valid Graph API access token.
	 * 
	 * @param args
	 *            Command-line arguments.
	 * @throws IOException
	 * @throws IllegalArgumentException
	 *             If no command-line arguments are provided.
	 */
	public static void main(String[] args) throws IOException {

		// if (args.length == 0)
		// throw new IllegalArgumentException(
		// "You must provide an OAuth access token parameter. " + "See README
		// for more information.");
		String tocken = "EAACEdEose0cBACPQ7N6P56QlkB9Bj7duzdQTCk6SDaQpQMttUyvjkQpLvnzQrZBMD3P1i5iOl7Kui94gaZBjfy0gHDYdKtOyZAN5RF1cRa7XyMO6KMd54EZA2ZCT0Fn7GdyzBe0cXxdkglQQH7S4D59BnJByi0NWs4KytxST4sMADqds9czRyHcw66xKSqHgZD";
//		String tocken = "EAACEdEose0cBABYUzMmIR1BEILAXDnYQ1T16sh8dUqRLluwAtZCGDV4GYsx5ZBoYVPmZAo43B3KGhjcrNWxKJmU2FdjgQ2i0yQ8X3cSzlUOpnnFxVXTr25ZC946Nw1q8xhq3vSZBNF2I9ZCHzKfc34Ll4MF1npZA1pISXxWoyTnHAZDZD";
		// "CAAC2PiFYZANMBAAoQpaj1qA3XTYZAZA4jwQbYZB3tsQV1R5qRzgyU7KnqdjOM3lXVgWJFJMKFnvNR0Xq9eTNp1z7r4AeGXORGrNboC8NTG6VrvrzjrxpaOjxbiZAD4LGZAYz5bHRJc5KMclLzVhnzTde3E4Ral1FLQxgcXhF0ZB9xkz9QjkoYQR";
		new GraphReaderExample(tocken).runEverything();
	}

	GraphReaderExample(String accessToken) {
		facebookClient23 = new DefaultFacebookClient(accessToken, Version.VERSION_2_5);
		// facebookClient20 = new DefaultFacebookClient(accessToken,
		// Version.VERSION_2_0);
	}

	void runEverything() throws IOException {
//		 getFriendList("me", 0);
//		fetchPage("groups/VietnamWorksRecruitersCommunity/");
//		fetchPage("groups/jobseeker.vn");
		fetchPage("vieclam24");
		fetchPage("vieclam24h.vn");
		fetchPage("thanhnien");
		fetchPage("ITviec");
		
		
		
		
		// fetchObject();
		// fetchObjects();
		// fetchObjectsAsJsonObject();
		// fetchConnections();
		// fetchDifferentDataTypesAsJsonObject();
		// query();
		// multiquery();
		// search();
		// metadata();
		// paging();
		// selection();
		// parameters();
		// rawJsonResponse();
	}

	/*
	 * TODO: get user, save to file, design file structure. Data should to
	 * get:???
	 * 
	 */
	void getFriendList(String userName, int level) throws IOException {
		System.out.println("get friend");
		Connection<User> myFriends = facebookClient23.fetchConnection(userName + "/invitable_friends", User.class,
				Parameter.with("fields", "id,name,about,birthday"));// "id,first_name,last_name,name"));
		// JsonObject json = facebookClient23.fetchObject("me/permissions",
		// JsonObject.class);// ,

		try {

			while (myFriends.hasNext()) {
				List<User> users = myFriends.getData();
				// System.out.println("get friend " + users.size() + " " +
				// myFriends.getTotalCount());
				System.out.println("get friend " + users.size() + " " + myFriends.getTotalCount() + "\n");
				for (User fr : users) {
					System.out.println("get friend\n");
					fw.write("User name: " + fr.getName() + "|" + fr.getAbout() + "|" + fr.getEmail() + "|"
							+ fr.getHometownName() + "|" + fr.getBio() + "|" + fr.getId() + "\n");
					out.println("User name: " + fr.getName() + " " + fr.getId() + "\n");
					
					Connection<User> targetedSearch = facebookClient23.fetchConnection("search", User.class,
							Parameter.with("q", fr.getName()), Parameter.with("type", "user"));
					for (User u : targetedSearch.getData()) {
						fw.write( "search "+u.getId()+"---"+u.getName()+"\n");
					}
					
					// try {
					// Connection<User> myFriends2 =
					// facebookClient23.fetchConnection(fr.getId() +
					// "/invitable_friends", User.class,
					// Parameter.with("fields", "id,name"));
					// for (User fr2 : myFriends2.getData()) {
					// out.println("User name: " + fr2.getName() + "\n");
					// fw.write("----Friend of User name: " + fr2.getName() +
					// "\n");
					// }
					//// String query="SELECT uid, name FROM user WHERE uid IN
					// (SELECT uid2 FROM friend WHERE uid1 IN (SELECT uid FROM
					// user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 =
					// "+fr.getId()+" ) and is_app_user=1) )";
					//// String query = "SELECT uid, name FROM user WHERE
					// uid="+fr.getId();
					//// List<FqlUser> usersr =
					// facebookClient23.executeFqlQuery(query, FqlUser.class);
					//
					//// out.println("Users: " + usersr);
					// } catch (Exception e) {
					// e.getMessage();
					// }
				}
				out.println(myFriends.getNextPageUrl());
				myFriends = facebookClient23.fetchConnectionPage(myFriends.getNextPageUrl(), User.class);

			}
			List<User> users = myFriends.getData();
			for (User fr : users) {
				System.out.println("get friend\n");
				fw.write("User name: " + fr.getName() + "|" + fr.getFirstName() + "|" + fr.getEmail() + "|"
						+ fr.getHometownName() + "|" + fr.getBirthday() + "|" + fr.getId() + "\n");
				out.println("User name: " + fr.getName() + " " + fr.getId() + "\n");
			}
			fw.close();
		} catch (Exception e) {
			fw.close();
			e.getMessage();
		}
		// out.println("Users: " + users);
		// $user_info=$facebook->api(array('method'=>'fql.query',
		// 'query'=>$query));
		// for(Iterator iterator=users.iterator();iterator.hasNext();)
		// {
		// System.out.println("get friend");
		// User user=(User)iterator.next();
		// System.out.println(user.getRelationshipStatus());
		// }
	}

	void fetchObject() {
		out.println("* Fetching single objects *");

		User user = facebookClient23.fetchObject("me", User.class);
		Page page = facebookClient23.fetchObject("cocacola", Page.class);

		out.println("User name: " + user.getName());
		out.println("Page likes: " + page.getLikes());
	}

	void fetchPage(String pageName) throws IOException {
		Page page = facebookClient23.fetchObject(pageName, Page.class);
		Connection<Post> pageFeed = facebookClient23.fetchConnection(page.getId() + "/feed", Post.class);
		try {
			// fw.write("sc");
			filename = "output/" + Long.toString(System.currentTimeMillis()) +"/"; // 
			new File(filename).mkdir();
			for (List<Post> feed : pageFeed) {
				// fw.write("\n\n\n\n\n" + feed.toString() + "\n\n\n\n");
				for (Post post : feed) {
					// PRINTING THE POST
					String nfilename = filename+ post.getId()+"PostMess.txt"; //Long.toString(System.currentTimeMillis()),  + post.getCreatedTime().toString().trim() 
					fileut = new File(nfilename);
					fw = new FileWriter(fileut);
					
					//fw.write("====================================================================================");
					fw.write("\n" + post.getMessage() + "\n");
					fw.close();
//					fw.write("Post Name: "+post.getName() + "\n");
//					fw.write("Story : "+post.getStory() + "\n");
//					fw.write("Caption : "+post.getCaption() + "\n");
//					fw.write("Description : "+post.getDescription() + "\n");
//					fw.write("Source : "+post.getSource() + "\n");
//					fw.write("Attribution : "+post.getAttribution() + "\n");
//					fw.write("LikesCount : "+post.getLikesCount() + "\n");
//					fw.write("SharesCount : "+post.getSharesCount() + "\n");
//					fw.write("From user : "+post.getFrom().getName() + "\n");
//					fw.write("Category : "+post.getFrom().getCategory() + "\n");
//					fw.write("CreatedTime : "+post.getCreatedTime() + "\n");
//					fw.write("Place : "+post.getPlace() + "\n");
//					fw.write("Caption : "+post.getWithTags(). + "\n");
//					fw.write("-----------------------------------------------");
					
//					nfilename = filename+ post.getId()+"_commentList.txt"; 
//					getAllPostComments(post.getId(), facebookClient23, nfilename);
					
//					fw.write("\n\n\n\n\n");
					
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Getting posts:

	}

	void getAllPostComments(String postId, FacebookClient client, String nfilename) {
		int currentCount = 0;
		JsonObject jsonObject = client.fetchObject(postId + "/comments", JsonObject.class,
				Parameter.with("summary", true), Parameter.with("limit", 1));
		long commentsTotalCount = jsonObject.getJsonObject("summary").getLong("total_count");

		// System.out.println("\nComments:");
		try {
			// fw.write( + "\n");
			//Long.toString(System.currentTimeMillis()),  + post.getCreatedTime().toString().trim() 
			fileut = new File(nfilename);
			fw = new FileWriter(fileut);
			Connection<Comment> comments = null;
			boolean pom = true;
			while (pom == true) { // There should be "while(currentCount <
									// commentsTotalCount)" but currentCount is
									// always < then commentsTotalCount. That's
									// the
									// problem :)
				pom = false;
				comments = client.fetchConnection(postId + "/comments", Comment.class,
						Parameter.with("limit", 50000), Parameter.with("offset", currentCount));

				for (Comment komentar : comments.getData()) {
					pom = true;
					currentCount++;

					String mess = komentar.getMessage().replaceAll("\n", " ").replaceAll("\r", " ");
					if (mess != null) {
						fw.write("\n"+ komentar.getFrom().getId()+"#" + mess);
						
//						fw.write("\nComments:    [" + currentCount + "]: " + komentar.getFrom().getName() + "<"+komentar.getFrom().getId()+"> #"
//								+ mess + "\n");
						
//						fw.write("\nComments tag:    [" + currentCount + "]: " + komentar.getFrom(). + "<"+komentar.getFrom().getId()+"> ## "
//								+ mess + "\n");
//						System.out
//								.println("    [" + currentCount + "]: " + komentar.getFrom().getName() + " ## " + mess);
					}
				}
			}
			fw.close();
//			do {
//				comments = client.fetchConnection(comments.getNextPageUrl(), Comment.class,
//						Parameter.with("limit", 50000), Parameter.with("offset", currentCount));
//
//				for (Comment komentar : comments.getData()) {
//					// pom = true;
//					currentCount++;
//
//					String mess = komentar.getMessage().replaceAll("\n", " ").replaceAll("\r", " ");
//					if (mess != null) {
//						fw.write("\nComments:    [" + currentCount + "]: " + komentar.getFrom().getName() + " ## "
//								+ mess + "\n");
//						System.out
//								.println("    [" + currentCount + "]: " + komentar.getFrom().getName() + " ## " + mess);
//					}
//				}
//			} while (comments.hasNext());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(currentCount + " / " + commentsTotalCount);
	}

	void fetchObjectsAsJsonObject() {
		out.println("* Fetching multiple objects at once as a JsonObject *");

		List<String> ids = new ArrayList<String>();
		ids.add("4");
		ids.add("http://www.imdb.com/title/tt0117500/");

		// Make the API call
		JsonObject results = facebookClient23.fetchObjects(ids, JsonObject.class);

		System.out.println(results.toString());

		// Pull out JSON data by key and map each type by hand.
		JsonMapper jsonMapper = new DefaultJsonMapper();
		User user = jsonMapper.toJavaObject(results.getString("4"), User.class);
		Url url = jsonMapper.toJavaObject(results.get("http://www.imdb.com/title/tt0117500/").toString(), Url.class);

		out.println("User is " + user);
		out.println("URL is " + url);
	}

	void fetchObjects() {
		out.println("* Fetching multiple objects at once *");

		FetchObjectsResults fetchObjectsResults = facebookClient23.fetchObjects(Arrays.asList("me", "cocacola"),
				FetchObjectsResults.class);

		out.println("User name: " + fetchObjectsResults.me.getName());
		out.println("Page likes: " + fetchObjectsResults.page.getLikes());
	}

	void fetchDifferentDataTypesAsJsonObject() {
		out.println("* Fetching different types of data as JsonObject *");

		JsonObject zuck = facebookClient23.fetchObject("4", JsonObject.class);
		out.println(zuck.getString("name"));

		JsonObject photosConnection = facebookClient23.fetchObject("me/photos", JsonObject.class);
		JsonArray photosConnectionData = photosConnection.getJsonArray("data");

		if (photosConnectionData.length() > 0) {
			String firstPhotoUrl = photosConnectionData.getJsonObject(0).getString("source");
			out.println(firstPhotoUrl);
		}

		String query = "SELECT uid, name FROM user WHERE uid=4 or uid=11";
		List<JsonObject> queryResults = facebookClient23.executeFqlQuery(query, JsonObject.class);

		if (!queryResults.isEmpty())
			out.println(queryResults.get(0).getString("name"));
	}

	/**
	 * Holds results from a "fetchObjects" call.
	 */
	public static class FetchObjectsResults {
		@Facebook
		User me;

		@Facebook(value = "cocacola")
		Page page;
	}

	void fetchConnections() {
		out.println("* Fetching connections *");

		Connection<User> myFriends = facebookClient23.fetchConnection("me/friends", User.class);
		Connection<Post> myFeed = facebookClient23.fetchConnection("me/feed", Post.class);

		out.println("Count of my friends: " + myFriends.getData().size());

		if (!myFeed.getData().isEmpty())
			out.println("First item in my feed: " + myFeed.getData().get(0).getMessage());
	}

	void query() {
		out.println("* FQL Query *");

		List<FqlUser> users = facebookClient23.executeFqlQuery("SELECT uid, name FROM user WHERE uid=4 or uid=11",
				FqlUser.class);

		out.println("User: " + users);
	}

	void multiquery() {
		out.println("* FQL Multiquery *");

		Map<String, String> queries = new HashMap<String, String>();
		queries.put("users", "SELECT uid, name FROM user WHERE uid=4 OR uid=11");
		queries.put("likers", "SELECT user_id FROM like WHERE object_id=122788341354");

		MultiqueryResults multiqueryResults = facebookClient23.executeFqlMultiquery(queries, MultiqueryResults.class);

		out.println("Users: " + multiqueryResults.users);
		out.println("People who liked: " + multiqueryResults.likers);
	}

	/**
	 * Holds results from an "executeQuery" call.
	 * <p>
	 * Be aware that FQL fields don't always map to Graph API Object fields.
	 */
	public static class FqlUser {
		@Facebook
		String uid;

		@Facebook
		String name;

		@Override
		public String toString() {
			return format("%s (%s)", name, uid);
		}
	}

	/**
	 * Holds results from an "executeQuery" call.
	 * <p>
	 * Be aware that FQL fields don't always map to Graph API Object fields.
	 */
	public static class FqlLiker {
		@Facebook("user_id")
		String userId;

		@Override
		public String toString() {
			return userId;
		}
	}

	/**
	 * Holds results from a "multiquery" call.
	 */
	public static class MultiqueryResults {
		@Facebook
		List<FqlUser> users;

		@Facebook
		List<FqlLiker> likers;
	}

	void search() {
		out.println("* Searching connections *");

		// Connection<Post> publicSearch =
		// facebookClient23.fetchConnection("search", Post.class,
		// Parameter.with("q", "watermelon"),
		// Parameter.with("type", "post"));

		Connection<User> targetedSearch = facebookClient23.fetchConnection("search", User.class,
				Parameter.with("q", "Mark"), Parameter.with("type", "user"));

		// if (publicSearch.getData().size() > 0)
		// out.println("Public search: " +
		// publicSearch.getData().get(0).getMessage());

		out.println("Posts on my wall by friends named Mark: " + targetedSearch.getData().size());
	}

	void metadata() {
		out.println("* Metadata *");

		User userWithMetadata = facebookClient23.fetchObject("me", User.class, Parameter.with("metadata", 1));

		out.println("User metadata: has albums? " + userWithMetadata.getMetadata().getConnections().hasAlbums());
	}

	void paging() {
		out.println("* Paging support *");

		Connection<User> myFriends = facebookClient23.fetchConnection("me/friends", User.class);
		Connection<Post> myFeed = facebookClient23.fetchConnection("me/feed", Post.class, Parameter.with("limit", 100));

		out.println("Count of my friends: " + myFriends.getData().size());

		if (!myFeed.getData().isEmpty())
			out.println("First item in my feed: " + myFeed.getData().get(0));

		for (List<Post> myFeedConnectionPage : myFeed)
			for (Post post : myFeedConnectionPage)
				out.println("Post from my feed: " + post);
	}

	void selection() {
		out.println("* Selecting specific fields *");

		User user = facebookClient23.fetchObject("me", User.class, Parameter.with("fields", "id,name"));

		out.println("User name: " + user.getName());
	}

	void parameters() {
		out.println("* Parameter support *");

		Date oneWeekAgo = new Date(currentTimeMillis() - 1000L * 60L * 60L * 24L * 7L);

		Connection<Post> filteredFeed = facebookClient23.fetchConnection("me/feed", Post.class,
				Parameter.with("limit", 3), Parameter.with("until", "yesterday"), Parameter.with("since", oneWeekAgo));

		out.println("Filtered feed count: " + filteredFeed.getData().size());
	}

	void rawJsonResponse() {
		out.println("* Raw JSON *");
		out.println("User object JSON: " + facebookClient23.fetchObject("me", String.class));
	}
}