package com.android.management_linen.helpers;

import com.android.management_linen.models.DetailsScanBersih;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScanGrouper {

    public static List<GroupedScan> group(List<DetailsScanBersih> rawList) {
        Map<String, GroupedScan> groupedMap = new LinkedHashMap<>();

        for (DetailsScanBersih dtl : rawList) {
            String key = dtl.getSubCategoryName() + "|" + dtl.getUkuranName() + "|" + dtl.getWarnaName();
            GroupedScan group = groupedMap.get(key);
            if (group == null) {
                group = new GroupedScan(
                        dtl.getSubCategoryName(),
                        dtl.getWarnaName(),
                        dtl.getUkuranName(),
                        dtl.getBarangruangRuangNama(),
                        dtl.getBatascuci()
                );
            }
            group.setJumlah(group.getJumlah() + 1);
            group.setTotalBerat(group.getTotalBerat() + dtl.getBerat());
            groupedMap.put(key, group);
        }

        return new ArrayList<>(groupedMap.values());
    }
}
