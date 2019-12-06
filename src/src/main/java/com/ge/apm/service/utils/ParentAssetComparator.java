package com.ge.apm.service.utils;

import com.ge.apm.domain.AssetInfo;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.springframework.util.StringUtils;

import java.util.Comparator;

public class ParentAssetComparator implements Comparator<AssetInfo> {

    private String query;

    public ParentAssetComparator(String query) {
        this.query = query;
    }

    @Override
    public int compare(AssetInfo asset1, AssetInfo asset2) {
        if (StringUtils.isEmpty(query)) {
            return 0;
        }

        if (asset1.getName().contains(query)
                || asset1.getNamePyCode() != null   && asset1.getNamePyCode().contains(query.toLowerCase())
                || asset1.getQrCode() != null       && asset1.getQrCode().contains(query)
                || asset1.getFinancingNum() != null && asset1.getFinancingNum().contains(query)
                || asset1.getSerialNum() != null    && asset1.getSerialNum().contains(query)
                || (asset1.getDepartNum() != null   && asset1.getDepartNum().contains(query))) {

            return 1;
        }

        if (asset2.getName().contains(query)
                || asset2.getNamePyCode() != null   && asset2.getNamePyCode().contains(query.toLowerCase())
                || asset2.getQrCode() != null       && asset2.getQrCode().contains(query)
                || asset2.getFinancingNum() != null && asset2.getFinancingNum().contains(query)
                || asset2.getSerialNum() != null    && asset2.getSerialNum().contains(query)
                || (asset2.getDepartNum() != null   && asset2.getDepartNum().contains(query))) {

            return -1;
        }

        return 0;

    }

}
