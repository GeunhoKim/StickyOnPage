package com.inha.stickyonpage.db;

import org.apache.cassandra.thrift.*;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.thrift.TException;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * @author  Geunho Khim
 * @created 10/11/13, 6:43 PM
 * @updated 12/2/13
 *
 *  Cassandra database controller for Sticky On Page
 */
public class DBConnectionModule {

  private static DBConnectionModule instance = new DBConnectionModule();
  private static Connector thriftConnector = new Connector();

  public static DBConnectionModule getInstance() {
    return instance;
  }

  // Connection method. (port / hostname / keyspace) are depend on the environment.
  public static Connection getConnection() throws Exception {
    return getConnection("165.246.44.92", "sop_db_1", "9160");
  }

  public static Connection getConnection(String _hostname, String _keyspace, String _port) throws Exception {
    Connection conn;
    final String port = _port;
    final String hostname = _hostname;
    final String keyspace = _keyspace;

    Class.forName("org.apache.cassandra.cql.jdbc.CassandraDriver");
    conn = DriverManager.getConnection("jdbc:cassandra://" + hostname +":"+ port +"/"+ keyspace);

    return conn;
  }

  /**
   *
   * @param   url, userID, sticky, user name, conn
   * @throws  SQLException
   *
   *  insert sticky memo to the "Sticky" cf.
   *    cf: Sticky
   *      key: url,userID,created {
   *       (column: "user_id",   value: userID)
   *       (column: "user_name", value: userName)
   *       (column: "sticky",    value: sticky)
   *       (column: "created",   value: timestamp)
   *      }
   *
   *  write data below:
   *   - insert sticky to Sticky cf
   *   - update(insert) URL --> sticky_counter++
   *   - update uptodate column of User cf
   *   - add url to User cf
   */
  public void writeSticky(String url, String userID, String sticky, String user_name, Connection conn) throws SQLException {
    Statement stmt = null;

    try {
      stmt = conn.createStatement();
      long ts = System.currentTimeMillis();

      String query = "insert into \"Sticky\"(url, user_id, sticky, created, like, user_name) values"
              +"('"+ url +"','"+ userID +"','"+ sticky +"',"+ ts +","+ 0 +",'"+ user_name +"');";
      stmt.executeUpdate(query);

      // additional update methods
      addURL(url, 0, 1, conn); // update url sticky count
      updateUptodate(userID, user_name, ts, url, sticky, conn); // update user's latest sticky
      addUrlToUser(userID, url); // add user's url list

    }  catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();
  }

  /**
   *
   * @param   url, userID, created, sticky(updated content), conn
   * @throws  SQLException
   *
   *   update sticky's content
   *  스티키를 텝 했을때 Sticky 객체로부터 url, userID, timestamp 들을 가져온다. 이 세 항목이 하나의 스티키를 나타내는 composite key 이다.
   *  writeSticky 메소드와 다른점은 addURL 메소드를 호출하지 않는다는 점이다. (URL의 스티키 카운트는 변동 없으므로)
   *  또한 timestamp(created) 는 composite key의 일부이므로 업데이트 될 수 없다.
   *
   *  --> 스티키 내용의 갱신 기능은 생략할 예정. 따라서 해당 메소드는 null 값의 user_name 컬럼을 갱신하는데 이용한다.
   */
  @Deprecated
  public void updateSticky(String url, String userID, String user_name, long created, Connection conn) throws SQLException {
    Statement stmt = null;

    try {
      stmt = conn.createStatement();

      String query = "update \"Sticky\" set  user_name = '" + user_name + "'" +
              " where url = '" + url + "' and user_id = '" + userID + "' and created = " + created +";";

      stmt.executeUpdate(query);
    } catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();
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
      String query = "select user_id, user_name, created, like, sticky from \"Sticky\" where url = '" + url + "' and user_id = '" + user +"';";
      rs = stmt.executeQuery(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    List<Sticky> stickies = new ArrayList<Sticky>();

    while(rs.next()) {
      Sticky sticky = new Sticky();
      sticky.setURL(url);
      sticky.setUser(rs.getString(1));
      sticky.setUserName(rs.getString(2));
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
   * @param   url, conn
   * @return  all stickies of url
   * @throws  SQLException
   *
   *  url의 모든 스티키를 가져온다.
   */
  public List<Sticky> getAllStickies(String url, Connection conn) throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;

    try {
      stmt = conn.createStatement();
      String query = "select user_id, user_name, created, like, sticky from \"Sticky\" where url = '" + url + "';";
      rs = stmt.executeQuery(query);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    List<Sticky> stickies = new ArrayList<Sticky>();

    while(rs.next()) {
      Sticky sticky = new Sticky();
      sticky.setURL(url);
      sticky.setUser(rs.getString(1));
      sticky.setUserName(rs.getString(2));
      sticky.setTimestamp(rs.getDate(3));
      sticky.setLike(rs.getInt(4));
      sticky.setMemo(rs.getString(5));
      stickies.add(sticky);
    }

    rs.close();
    stmt.close();

    return stickies;
  }

  public List<Sticky> getAllStickies(Connection conn) throws SQLException {
	    Statement stmt = null;
	    ResultSet rs = null;

	    try {
	      stmt = conn.createStatement();
	      String query = "select user_id, user_name, created, like, sticky, url from \"Sticky\";";
	      rs = stmt.executeQuery(query);
	    } catch (SQLException e) {
	      e.printStackTrace();
	    }

	    List<Sticky> stickies = new ArrayList<Sticky>();

	    while(rs.next()) {
	      Sticky sticky = new Sticky();
	      sticky.setUser(rs.getString(1));
	      sticky.setUserName(rs.getString(2));
	      sticky.setTimestamp(rs.getDate(3));
	      sticky.setLike(rs.getInt(4));
	      sticky.setMemo(rs.getString(5));
	      sticky.setURL(rs.getString(6));
	      stickies.add(sticky);
	    }

	    rs.close();
	    stmt.close();

	    return stickies;
	}

  /**
   *
   * @param   url, num of extracted, num of sticky
   * @param   conn
   *
   *  insert url to the "URL" cf
   *    cf: URL
   *      key: url {
   *        (column: "url",           value: url)
   *        (column: "extract_count", value: counter)
   *        (column: "sticky_count",  value: counter)
   *      }
   *
   *  method usage:
   *   - insertion of counter CF is done by update query, not insert.
   *   - update counters. count++ is done by parameter s_count as 1.
   *   - e_count is used when text extractor extracts url.
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

  /**
   *
   * @param   user_id, other user id, url,
   * @param   timestamp of sticky, connection
   * @throws  SQLException
   *
   *  add preference to specific sticky, column like++ of Sticky CF.
   *    cf: Preference
   *      key: user_id
   *        (column: f_id:url:created, value: null)
   *
   *  method usage:
   *   - store preferences of users (like logs)
   *   - count like column in Sticky cf
   *
   *   additional check:
   *   - if user already press like button on sticky, than return false
   *   - if addPreference is succeed, return true
   */
  public boolean addPreference(String user_id, String f_id, String url, long created, Connection conn) throws SQLException {
    Statement stmt = null;

    try {
      stmt = conn.createStatement();

      // check if preference already exists
      if(isPrefExist(user_id, f_id, url, stmt)) {
        return false;
      }

      // count like column in Sticky cf
      countLike(url, f_id, created, stmt);

      long ts = System.currentTimeMillis();
      String query = "insert into \"Preference\"(user_id, f_id, url, created) values('" + user_id +"','"+ f_id + "','" + url + "'," + ts + ");";
      System.out.println(query);
      int m = stmt.executeUpdate(query);
      System.out.println("insert return value : "+m);
      
    } catch (SQLException e) {
      e.printStackTrace();
    }

    stmt.close();

    return true;
  }

  private boolean isPrefExist(String user_id, String f_id, String url, Statement stmt) throws SQLException {
    String query = "select count(*) from \"Preference\" where user_id = '" + user_id +"' and f_id = '" + f_id + "' and url = '" + url + "';";
    if(stmt.executeQuery(query).getInt(1) > 0)
      return true;

    return false;
  }

  private void countLike(String url, String userID, long created, Statement stmt) throws SQLException {

    String query = "select like from \"Sticky\" where url = '" + url + "' and user_id = '" + userID + "' and created = " + created + ";";
    System.out.println(query);
    int likeCount = stmt.executeQuery(query).getInt(1);
    String updateQuery = "update \"Sticky\" set like = " + (likeCount + 1) +
            " where url = '" + url + "' and user_id = '" + userID + "' and created = " + created + ";";
    stmt.executeUpdate(updateQuery);
  }

  /**
   *
   * @param   limit, connection
   * @return  list of URL
   * @throws  SQLException
   *
   *  get URL list limit (for test)
   */
  @Deprecated
  public List<String> getURLs(int limit, Connection conn) throws SQLException {
    List<String> urls = new ArrayList<String>();
    Statement stmt = null;
    ResultSet rs = null;

    try {
      stmt = conn.createStatement();
      String query = "select key from \"URL\" limit " + limit;
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

  /**
   *
   * @param   url
   * @return  similar url list
   *
   *  get most similar urls of target url.
   *
   * cf: Recommendation
   *  key: target url
   *    (column name: similar url, value: similarity)
   *
   *    반환 타입이 Map으로, 유사도를 key 값으로 오름차순 정렬되어 있다. TreeMap으로 casting 하여 반환한다면
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

  /**
   *
   * @param   user_id, user_name, conn
   * @throws  SQLException
   *
   *  insert user info when logging in the SOP. before insertion, check if user exists.
   */
  public void insertUser(String user_id, String user_name, Connection conn) throws SQLException {
    Statement stmt = null;

    if(!isUserExist(user_id, conn)) {
      try {
        stmt = conn.createStatement();
        String query = "insert into \"User\"(key, user_name, sticky_count) values ('" + user_id + "', '" + user_name + "', " + 0 + ");";
        stmt.executeUpdate(query);

      } catch (Exception e) {
        e.printStackTrace();
      }

      stmt.close();
    }
  }

  /**
   *
   * @param   userID, url
   * @throws  TException
   * @throws  InvalidRequestException
   * @throws  UnsupportedEncodingException
   * @throws  UnavailableException
   * @throws  TimedOutException
   */
  private void addUrlToUser(String userID, String url)
          throws TException, InvalidRequestException, UnsupportedEncodingException, UnavailableException, TimedOutException {
    Cassandra.Client client = thriftConnector.connect();
    String columnFamily = "User";
    ColumnParent columnParent = new ColumnParent(columnFamily);

    Column column = new Column();
    column.setName(ByteBufferUtil.bytes(url));
    column.setValue(new byte[0]);
    column.setTimestamp(System.currentTimeMillis());

    client.insert(ByteBufferUtil.bytes(userID), columnParent, column, ConsistencyLevel.ONE);
  }

  /**
   *
   * @param   user_id, conn
   * @return  bool
   * @throws  SQLException
   *
   *  check if user info already exists before insert user cf
   */
  private boolean isUserExist(String user_id, Connection conn) throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;

    try {
      stmt = conn.createStatement();
      String query = "select count(*) from \"User\" where key = '" + user_id + "';";
      rs = stmt.executeQuery(query);

      if(rs.getInt(1) == 1) {
        return true;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    rs.close();
    stmt.close();

    return false;
  }

  /**
   *
   * @param   user_id, created, url, content, conn
   * @throws  SQLException
   *
   *  update uptodate column in User cf. used in writeSticky method
   * uptodate 컬럼의 데이터는 created::url::content 의 형식으로 저장된다. (초기 화면에서 이용할 때 '::' 로 스트링을 파싱한다.)
   */
  public void updateUptodate(String user_id, String user_name, long created, String url, String content, Connection conn) throws SQLException {
    Statement stmt = null;

    try {
      stmt = conn.createStatement();
      String query = "update \"User\" set sticky_count = " +(getUserStickyCount(user_id, conn) + 1)+
              ", uptodate = '" + created + "::" + url + "::" + user_name + "::" + content + "' where key = '" + user_id + "';";
      stmt.executeUpdate(query);

    } catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();
  }

  /**
   *
   * @param   userID, conn
   * @return  user's sticky count
   * @throws  SQLException
   *
   *  used in updateUptodate method
   */
  public int getUserStickyCount(String userID, Connection conn) throws SQLException {
    Statement stmt = null;
    int sticky_count = 0;

    try {
      stmt = conn.createStatement();
      String query = "select sticky_count from \"User\" where key = '" + userID + "';";
      sticky_count = stmt.executeQuery(query).getInt(1);

    } catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();

    return sticky_count;
  }

  /**
   *
   * @param   userID, conn
   * @return  latest sticky
   * @throws  SQLException
   *
   *  get latest sticky of User cf. SOP 의 처음 페이지에서 사용된다.
   */
  public Sticky getLatestSticky(String userID, Connection conn) throws SQLException {
    Statement stmt = null;
    Sticky sticky = null;
    String uptodate;

    try {
      stmt = conn.createStatement();
      String query = "select uptodate from \"User\" where key = '" + userID + "';";
      uptodate = stmt.executeQuery(query).getString(1);
      sticky = parseUptodate(uptodate);
      sticky.setUser(userID);

    } catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();

    return sticky;
  }

  private Sticky parseUptodate(String uptodate) {
    Sticky sticky = new Sticky();
    String[] parsed = uptodate.split("::");

    sticky.setTimestamp(new Date(Long.parseLong(parsed[0])));
    sticky.setURL(parsed[1]);
    sticky.setUserName(parsed[2]);
    sticky.setMemo(parsed[3]);

    return sticky;
  }

  /**
   *
   * @param   url, conn
   * @return  sticky count of url
   * @throws  SQLException
   *
   *  get sticky_count column from URL cf
   */
  public int getURLStickyCount(String url, Connection conn) throws SQLException {
    Statement stmt = null;
    ResultSet rs = null;
    int count = 0;

    try {
      stmt = conn.createStatement();
      String query = "select sticky_count from \"URL\" where key = '" + url + "';";
      rs = stmt.executeQuery(query);
      count = rs.getInt(1);

    } catch (Exception e) {
      e.printStackTrace();
    }

    rs.close();
    stmt.close();

    return count;
  }

  /**
   *
   * @param   list of friends, conn
   * @return  friend list who registered in User cf
   * @throws  SQLException
   *
   *  get friends of Sticky On Page user.
   *
   *  usage:
   *    friends = dbcm.getFriendOfSOPUser(friends, dbcm.getConnection());
   *
   *  method removes all non SOP user from parameter of friend list
   */
  public HashSet<String> getFriendsOfSOPUser(HashSet<String> friends, Connection conn) throws SQLException {
    Iterator<String> it = friends.iterator();

    while(it.hasNext()) {
      String user_id = it.next();
      if(!isUserExist(user_id, conn)) { // if user does not exist,
        it.remove();                    //+ remove that user from list
      }
    }

    return friends;
  }

  /**
   *
   * @param   column family, conn
   * @return  count of column family
   * @throws  SQLException
   *
   *  get count of column family.
   *
   *  Sticky On Page's cf = {Sticky, User, URL, Recommendation, Preference}
   *    warning: case sensitive!
   */
  public int getCFCount(String cf, Connection conn) throws SQLException {
    Statement stmt = null;
    int count = 0;

    try {
      stmt = conn.createStatement();
      String query = "select count(*) from \"" + cf + "\"";
      count = stmt.executeQuery(query).getInt(1);


    } catch (Exception e) {
      e.printStackTrace();
    }

    stmt.close();

    return count;
  }

}
