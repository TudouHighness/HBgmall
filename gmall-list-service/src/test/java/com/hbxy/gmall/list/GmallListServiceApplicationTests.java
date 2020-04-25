package com.hbxy.gmall.list;


import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testES() throws IOException {

        String query ="{\n" +
                "  \"query\": {\n" +
                "    \"match\": {\n" +
                "      \"skuName\": \"小米\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        // 在哪个index，type 中执行
        Search search = new Search.Builder(query).addIndex("gmall").addType("SkuInfo").build();
        // 执行search 查询动作
        SearchResult searchResult = jestClient.execute(search);

        // 获取数据
        List<SearchResult.Hit<Map, Void>> hits = searchResult.getHits(Map.class);

        for (SearchResult.Hit<Map, Void> hit : hits) {
            Map map = hit.source;
            System.out.println(map.get("skuName"));
        }
    }
}


