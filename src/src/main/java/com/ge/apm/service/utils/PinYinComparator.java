package com.ge.apm.service.utils;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class PinYinComparator implements Comparator<String> {
    Logger logger = LoggerFactory.getLogger(PinYinComparator.class);

    /**
     * why org1 and org2 will be null? because the old saved custom config contains some old fields not exists any more.
     * @param org1
     * @param org2
     * @return
     */
    @Override
    public int compare(String org1, String org2) {
        try {
<<<<<<< Updated upstream
            if (null == org1 && null == org2) {
                return 0;
            } else if (null == org1) {
                return -1;
            } else if (null == org2){
=======
            if(org1==null && org2==null) {
                return 0;
            } else if (org1 == null) {
                return -1;
            } else if (org2 == null) {
>>>>>>> Stashed changes
                return 1;
            } else {
                String name1 = PinyinHelper.convertToPinyinString(org1, "", PinyinFormat.WITHOUT_TONE);
                String name2 = PinyinHelper.convertToPinyinString(org2, "", PinyinFormat.WITHOUT_TONE);
                return name1.compareTo(name2);
            }
<<<<<<< Updated upstream

=======
>>>>>>> Stashed changes
        }catch(Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

}
