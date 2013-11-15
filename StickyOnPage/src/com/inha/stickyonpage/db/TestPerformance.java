import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Geunho Khim
 * Date: 11/5/13
 * Time: 11:10 PM
 *
 *  데이터베이스의 성능을 측정하기 위한 테스트 클래스
 */
public class TestPerformance {
  // index 0 은 사용하지 않는다. index는 url을, 값은 해당 url에 달린 스티키 갯수를 나타낸다.
  public static int[] m = {0, 10, 25, 50, 100, 250, 500};

  /**
   *
   * @param number of users
   * @return user hash set
   *
   *  numUsers 크기의 랜덤 유저 해시 셋을 반환한다.
   */
  public static HashSet<Integer> getUsers(int numUsers) {
    HashSet<Integer> users = new HashSet<Integer>();

    while(users.size() != numUsers)
      users.add((int) (Math.random() * 1000) + 1);

    return users;
  }

  /**
   *  url 의 스티키 갯수: m = {10, 25, 50, 100, 250, 500}
   *  입력될 유저: 1 ~ 1000
   *
   *   935개의 쓰기 쿼리를 수행하는데 1초도 걸리지 않았다.
   */
  @Test
  public void putTestData() throws Exception {
    DBConnectionModule dbcm = DBConnectionModule.getInstance();
    Connection conn = dbcm.getConnection();

    String url;
    String user;

    for(int i = 1; i < m.length; i++) {
      url = String.valueOf(i);
      for(int j = 0; j < m[i]; j++) {
        user = String.valueOf((int) (Math.random() * 1000) + 1); // 1 ~ 1000까지 랜덤한 숫자를 유저 아이디로 한다.
        dbcm.writeSticky(url, user, "스티키테스트: "+user, conn);
      }
    }
  }

  @Test
  public void testGetStickies1() throws Exception {
    DBConnectionModule dbcm = DBConnectionModule.getInstance();
    Connection conn = dbcm.getConnection();

    // 파라미터
    String url = "6"; // 1 ~ 6
    // 유저 리스트 수: n = {10, 25, 50, 100, 250, 500, 1000}
    int numUsers = 1000;
    ArrayList<Sticky> stickies = new ArrayList<Sticky>();

    // 랜덤하게 유저 목록을 가져온다.
    HashSet<Integer> users = getUsers(numUsers);

    long start = System.currentTimeMillis();

    for(Integer user : users) {
      stickies.addAll(dbcm.getStickies(url, String.valueOf(user), conn));
    }

    long end = System.currentTimeMillis();

    for(Sticky sticky : stickies) {
      System.out.println(sticky.toString());
    }
    System.out.println("Time spent = " + (end - start) + "ms");
  }

}
