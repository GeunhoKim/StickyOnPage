package com.inha.stickyonpage.db;

import org.apache.cassandra.thrift.*;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.thrift.TException;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author  Geunho Khim
 * @created 10/11/13, 6:43 PM
 * @updated 11/21/13
 *
 *  test module to test Cassandra I/O
 */
public class DBConnectionModule {
  private static DBConnectionModule instance = new DBConnectionModule();
  private static Connector thriftConnector = new Connector();

  public static DBConnectionModule getInstance() {
    return instance;
  }

  // Connection method. (port / hostname / keyspace) are depend on the environment.
  public static Connection getConnection() throws Exception {
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
            "geunho.khim@gmail.com", "합격을 기원합니다.", conn);

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
   *  url의 모든 스티키를 가져온다.
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
   * [NAHEON]
   * This is a temporal method for Recent sticky fragment.
   */
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
    url = url.replace("'", "%27"); // apostrophe 를 %27로 모두 치환한다.
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
   *    첫 primary key가 유저 아이디이므로 한 유저의 모든 선호도를 바로 가져올 수 있는 장점이 있다.
   *
   *  TODO: Sticky cf의 like 컬럼을 increment 해야한다.
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
   * @param   limit, connection
   * @return  list of URL
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

  /**
   *
   * @param   url
   * @return  similar url list
   *
   *  get most similar urls of target url.
   *
   * CF: Recommendation
   *  RowKey: target url
   *    (column name: similar url, value: similarity)
   *
   *   반환 타입이 Map으로, 유사도를 key 값으로 오름차순 정렬되어 있다. TreeMap으로 casting 하여 반환한다면
   *   pollLastEntry() 메소드로 큰 값부터 가져올 수 있다.
   *
   */
  public Map<Double, String> getRecommendation(String url)
          throws TException, InvalidRequestException, UnavailableException, TimedOutException, CharacterCodingException {
    TreeMap<Double, String> recommendList = new TreeMap<Double, String>();
    Cassandra.Client client = thriftConnector.connect();
    ByteBuffer key = ByteBufferUtil.bytes(url);

    SlicePredicate predicate = new SlicePredicate();
    SliceRange sliceRange = new SliceRange();
    sliceRange.setStart(new byte[0]);
    sliceRange.setFinish(new byte[0]);
    predicate.setSlice_range(sliceRange);

    String columnFamily = "Recommendation";
    ColumnParent parent = new ColumnParent(columnFamily);

    List<ColumnOrSuperColumn> cols = client.get_slice(key, parent, predicate, ConsistencyLevel.ONE);

    for (ColumnOrSuperColumn cosc : cols) {
      Column column = cosc.column;
      String recommend = ByteBufferUtil.string(column.name);
      Double similarity = ByteBufferUtil.toDouble(column.value);

      recommendList.put(similarity, recommend);
    }

    return recommendList;
  }
  @Test
  public void testGetRecommendation()
          throws UnavailableException, TException, InvalidRequestException, TimedOutException, CharacterCodingException {
    String url = "http://en.wikipedia.org/wiki/$1_Money_Wars";
    Map<Double, String> recommends = getRecommendation(url);

    System.out.println(recommends.toString());
  }
}