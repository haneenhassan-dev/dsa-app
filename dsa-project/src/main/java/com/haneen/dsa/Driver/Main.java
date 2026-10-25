package com.haneen.dsa.Driver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode cases = mapper.readTree(Files.readString(Path.of(args[0])));

        Solution solution = new Solution();
        ExecutorService pool = Executors.newSingleThreadExecutor();
        List<Map<String,Object>> verdicts = new ArrayList<>();

        for (JsonNode tc : cases) {
            Map<String,Object> v = new LinkedHashMap<>();
            v.put("testcaseId", tc.get("testcaseId").asLong());

            JsonNode in    = tc.get("input");
            int[] nums     = mapper.convertValue(in.get("nums"), int[].class);
            int target     = in.get("target").asInt();
            int[] expected = mapper.convertValue(tc.get("expected"), int[].class);

            Future<int[]> f = pool.submit(() -> solution.twoSum(nums, target));
            long start = System.currentTimeMillis();
            try {
                int[] actual = f.get(2, TimeUnit.SECONDS);
                v.put("execTimeMs", System.currentTimeMillis() - start);
                v.put("actualOutput", Arrays.toString(actual));
                v.put("status", Arrays.equals(actual, expected) ? "AC" : "WA");
            } catch (TimeoutException e) {
                f.cancel(true);
                v.put("status", "TLE");
            } catch (ExecutionException e) {
                v.put("status", "RE");
                v.put("actualOutput", String.valueOf(e.getCause()));
            }
            verdicts.add(v);
        }
        pool.shutdownNow();
        Files.writeString(Path.of(args[1]), mapper.writeValueAsString(verdicts));
    }
}