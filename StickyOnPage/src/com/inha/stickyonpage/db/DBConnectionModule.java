package com.inha.stickyonpage.db;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Geunho Khim
 * Date: 10/11/13
 * Time: 6:43 PM
 *
 *  test module to test Cassandra I/O
 */
public class DBConnectionModule {
  private static DBConnectionModule instance;

  public static DBConnectionModule getInstance() {
	//Singletone pattern
	if(instance == null){
		instance = new DBConnectionModule();
	}
    return instance;
  }
  
  private DBConnectionModule(){
  }

  
  // Connection method. (port / hostname / keyspace) are depend on the environment.
  //public static Connection getConnection() throws Exception {
  public Connection getConnection() throws Exception {
    Connection conn = null;
    final String port = "9160";
    final String hostname = "165.246.44.92";
    final String keyspace = "sop_db_1";

    Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
    conn = DriverManager.getConnection("jdbc:cassandra://" + hostname +":"+ port +"/"+ keyspace);

    return conn;
  }

  /**
   *
   * @param url, userID, sticky, connection
   * @throws SQLException
   *
   *  Insert sticky memo to the "Sticky" CF. (by CQL)
   *    CF: Sticky
   *      Key: url,userID,created {
   *       (column_name: "userID", value: userID)
   *       (column_name: "sticky", value: sticky)
   *       (column_name: "created", value: timestamp)
   *      }
   *
   *  1. Insert Sticky
   *  2. Update(Insert) URL --> sticky_counter++
   *
   *  Todo: Too long rowkey. url should be shorten to the redirecting database
   */
  public void writeSticky(String url, String userID, String sticky, Connection conn) throws SQLException {
    Statement stmt = null;
    try {
      stmt = conn.createStatement();

      long ts = System.currentTimeMillis();

      String query = "insert into \"Sticky\"(url, user_id, sticky, created, like) values" +"('"+ url +"','"+ userID +"','"+ sticky +"',"+ ts +","+ 0 +");";

      //String query = "update Sticky set sticky = '" + sticky + "', like = " + 0 + " where url = '" + url + "' and user_id = '" + userID + "' and created = " + ts +";";
      stmt.execute(query);
      addURL(url, 0, 1, conn);
    }  catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();
  }
  @Test
  public void testWriteSticky() throws Exception {
    Connection conn = getConnection();
    writeSticky("http://wsnews.co.kr/society/index_view.php?zipEncode===am1udoX0tB152x3vwA2zImX0tB15KmLrxyJzsn90wDoftz0f2yMetpSfMvWLME",
            "geunho.khim@gmail.com", "dfad", conn);

  }


  public List<Sticky> getAllStickies(Connection conn) throws SQLException {
	    Statement stmt = null;
	    ResultSet rs = null;

	    try {
	      stmt = conn.createStatement();
	      String query = "select url, user_id, created, like, sticky from \"Sticky\"";
	      rs = stmt.executeQuery(query);
	    } catch (SQLException e) {
	      e.printStackTrace();
	    }

	    List<Sticky> stickies = new ArrayList<Sticky>();
	    
	    int count = 20;
	    while(rs.next() && (count-- >= 0)) {
	      Sticky sticky = new Sticky();
	      sticky.setURL(rs.getString(1));
	      sticky.setUser(rs.getString(2));
	      sticky.setTimestamp(rs.getDate(3));
	      sticky.setLike(rs.getInt(4));
	      sticky.setMemo(rs.getString(5));
	      stickies.add(sticky);
	    }

	    rs.close();
	    stmt.close();

	    return stickies;
  }
  
  /**
   *
   * @param url, user, connection
   * @return stickies of specific user
   * @throws SQLException
   *
   *  url�좎룞�쇿뜝�덌옙占쏙옙�좎럩伊쇿뜝�숈삕�좎룞�숋옙�덈폃�좎뜫�뉓キ�듭삕�좎뜴泥롥뜝�뚯，�좎뜦維�옙占�   */
  public List<Sticky> getStickies(String url, String user, Connection conn) throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;

    try {
      stmt = conn.createStatement();
      String query = "select user_id, created, like, sticky from \"Sticky\" where url = '" + url + "' and user_id = '" + user +"';";
      rs = stmt.executeQuery(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    List<Sticky> stickies = new ArrayList<Sticky>();

    while(rs.next()) {
      Sticky sticky = new Sticky();
      sticky.setURL(url);
      sticky.setUser(rs.getString(1));
      sticky.setTimestamp(rs.getDate(2));
      sticky.setLike(rs.getInt(3));
      sticky.setMemo(rs.getString(4));
      stickies.add(sticky);
    }

    rs.close();
    stmt.close();

    return stickies;
  }
  @Test
  public void testGetStickies() throws Exception {
    Connection conn = getConnection();
    List<Sticky> stickies = getStickies("http://wsnews.co.kr/society/index_view.php?zipEncode===am1udoX0tB152x3vwA2zImX0tB15KmLrxyJzsn90wDoftz0f2yMetpSfMvWLME", "geunho.khim@gmail.com", conn);
    System.out.println("(url, user_id, created, like, sticky)");
    for(Sticky sticky : stickies) {
      System.out.println(sticky.getURL() +", "+ sticky.getUserID() +", "+ sticky.getTimestamp() +", "+ sticky.getLike() +", "+ sticky.getMemo());
    }
  }

  /**
   *
   * @param url, conn
   * @return all stickies of url
   * @throws SQLException
   *
   *  url�좎룞�숂춯琉대�獄�옙�좎뜫�뗰옙�룹삕占쎌궪���띠룊�숃��쏆삕占썬끇堉�
   */
  public List<Sticky> getAllStickies(String url, Connection conn) throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;

    try {
      stmt = conn.createStatement();
      String query = "select user_id, created, like, sticky from \"Sticky\" where url = '" + url + "';";
      rs = stmt.executeQuery(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    List<Sticky> stickies = new ArrayList<Sticky>();

    while(rs.next()) {
      Sticky sticky = new Sticky();
      sticky.setURL(url);
      sticky.setUser(rs.getString(1));
      sticky.setTimestamp(rs.getDate(2));
      sticky.setLike(rs.getInt(3));
      sticky.setMemo(rs.getString(4));
      stickies.add(sticky);
    }

    rs.close();
    stmt.close();

    return stickies;
  }
  @Test
  public void testGetAllStickies() throws Exception {
    Connection conn = getConnection();
    List<Sticky> stickies = getAllStickies("http://en.wikipedia.org/wiki/Tf%E2%80%93idf", conn);
    System.out.println("(url, user_id, created, like, sticky)");
    for(Sticky sticky : stickies) {
      System.out.println(sticky.getURL() +", "+ sticky.getUserID() +", "+ sticky.getTimestamp() +", "+ sticky.getLike() +", "+ sticky.getMemo());
    }
  }

  /**
   *
   * @param url
   * @param conn
   *
   *  Insert url address to the "URL" CF. (by CQL)
   *    CF: URL
   *      RowKey: url {
   *        (column name: "url", value: url)
   *        (column name: "extract_count", value: counter)
   *        (column name: "sticky_count", value: counter)
   *      }
   *  1. insertion of counter CF is done by update query, not insert.
   *  2. count�좎룞�쇿뜝�덇턁占쎈��숋옙袁⑤콦
   */
  public void addURL(String url, int e_count, int s_count, Connection conn) throws SQLException {
    url = url.replace("'", "%27"); // apostrophe 占쎌뮋��27�β댙�숂춯琉대�筌륅옙�곸궡瑗뱄옙�룹삕占쎈베堉�
    Statement stmt = null;

    try {
      stmt = conn.createStatement();

      String query = "update \"URL\" set extract_count = extract_count +"+ e_count
              +", sticky_count = sticky_count + "+ s_count +" where KEY = '" + url + "';";
      stmt.executeUpdate(query);
    } catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();
  }
  @Test
  /**
   *  This test program execute inserting 5,716,808 url address of Wikipedia(en) to the URL CF.
   */
  public void testAddURL() throws Exception {
    long startTime = System.currentTimeMillis();

    String currentDir = System.getProperty("user.dir");
    FileReader fr = new FileReader(currentDir + "/resource/wiki-titles-sorted.txt");
    BufferedReader br = new BufferedReader(fr, 500); // buffer size is 500

    Connection conn = getConnection();
    String title;
    int count = 0;
    for(int i = 0; i < 1000; i++) {
      title = br.readLine();
      addURL("http://en.wikipedia.org/wiki/" + title, 0, 0, conn);
      count++;
      if(count%1000==0)
        System.out.print("1");
    }

    long endTime = System.currentTimeMillis();
    System.out.println("\nInserting " + count + " urls, took " + (endTime - startTime) + " milliseconds");
  }

  /**
   *
   * @param user_id, f_id, url, connection
   * @throws SQLException
   *
   *  Add preference to specific sticky, column like++ of Sticky CF.
   *    CF: Preference
   *      RowKey: user_id
   *        (column name: f_id:url:created, value: null)
   *
   *    嶺뚳퐦�셮rimary key�띠룊�쇿뜝�뚯쪠�좑옙�좎럡�댐옙醫묒삕�븐슦逾졿쾬�볦삕餓ο옙�좎룞�쇿뜝�뚯쪠�좎룞�쇿뜝�숈춻占썬끇援��좎럩伊싷옙�쏆삕熬곣뫅���꾩룆��빳占썸뤆�됱삕鈺곕돍�쇿뜝�숈삕�좎룞�숋옙�덈츎 �좎떥�깆젍�좎룞�쇿뜝�덈쐞占쏙옙
   *
   */
  public void addPreference(String user_id, String f_id, String url, Connection conn) throws SQLException {
    Statement stmt = null;

    try {
      stmt = conn.createStatement();

      long ts = System.currentTimeMillis();
      String query = "insert into \"Preference\"(user_id, f_id, url, created) values('" + user_id +"','"+ f_id + "','" + url + "','" + ts + "');";
      stmt.executeUpdate(query);

    } catch (SQLException e) {
      e.printStackTrace();
    }

    stmt.close();
  }
  @Test
  public void testAddPreference() throws Exception {
    Connection conn = getConnection();
    addPreference("geunho.khim@gmail.com", "rootkim0127@gmail.com", "http://en.wikipedia.org/wiki/Tf%E2%80%93idf", conn);
  }

  /**
   *
   * @param limit, connection
   * @return list of URL
   *
   * @throws SQLException
   *
   *  get URL list limit (for test)
   */
  public List<String> getURLs(int limit, Connection conn) throws SQLException {
    List<String> urls = new ArrayList<String>();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      stmt = conn.createStatement();
      String query = "select url from \"URL\" limit " + limit;
      stmt.executeQuery(query);
      rs = stmt.getResultSet();

    } catch (SQLException e) {
      e.printStackTrace();
    }

    while(rs.next()) {
      urls.add(rs.getString(1));
    }

    rs.close();
    stmt.close();

    return urls;
  }
  @Test
  public void testGetURLs() throws Exception {
    int limit = 10;
    Connection conn = getConnection();
    List<String> urls = getURLs(limit, conn);

    System.out.println(urls.toString());

  }
}
