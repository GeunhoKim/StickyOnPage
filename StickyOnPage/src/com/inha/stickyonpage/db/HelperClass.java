package com.inha.stickyonpage.db;

import java.util.*;

/**
 * @author  Geunho Khim
 * @created 11/26/13, 6:41 PM
 * @updated 11/26/13
 */
public class HelperClass {

  /**
   * @param   users, number of randoms
   * @return  list of random number of users
   */
  public static List<String> getRandFriends(List<String> users, int max) {
    List<String> result = new ArrayList<String>();
    int size = users.size();

    // if the size of friend list is smaller than max, just return friend list
    if(size <= max) {
      return users;

    } else {
      // hash set of random index
      HashSet<Integer> randFriends = new HashSet<Integer>();
      Random rand = new Random();

      while(randFriends.size() != max) {
        int idx = rand.nextInt(size);
        randFriends.add(idx);
      }

      for(Integer idx : randFriends) {
        result.add(users.get(idx));
      }
    }

    return result;
  }

  /**
   * @param   stickies
   * @return  sorted stickies
   *
   *  sort stickies by 'like' column.
   */
  public static List<Sticky> sortStickyByLike(List<Sticky> stickies) {

    Collections.sort(stickies, new Comparator<Sticky>() {
      @Override
      public int compare(Sticky sticky, Sticky sticky2) {
        int a = sticky.getLike();
        int b = sticky2.getLike();

        return a < b ? 1
                : a > b ? -1
                : 0;
      }
    });

    return stickies;
  }

}
