package com.inha.stickyonpage.db;

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

  // Connection method. (port / hostname / keyspace) are depend on the environment.
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
      stmt.execute(query);
      addURL(url, 0, 1, conn);
    }  catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();
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
   *  url의 특정 유저의 스티키들을 가져온다.
   */
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

  /**
   *
   * @param url, conn
   * @return all stickies of url
   * @throws SQLException
   *
<<<<<<< HEAD
   *  url의 모든 스티키를 가져온다.
=======
   *  url占쎌쥙猷욑옙�귥땡筌뚮�占썹뛾占쎌삕占쎌쥙�ワ옙�곗삕占쎈９�뺝뜝�뚭땔占쏙옙占쎈씈猷딉옙�껓옙占쎌룇�뺝뜝�щ걞�됵옙
>>>>>>> upstream/master
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
   *  2. count의 업데이트
   */
  public void addURL(String url, int e_count, int s_count, Connection conn) throws SQLException {
    url = url.replace("'", "%27"); // apostrophe를 %27로 모두 치환한다.
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
   *  첫 primary key가 유저 아이디이므로 한 유저의 모든 선호도를 바로 가져올 수 있는 장점이 있다.
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
}
