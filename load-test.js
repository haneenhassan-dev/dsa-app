import http from 'k6/http';
import { check } from 'k6';

export const options = {
    vus: 10, // 50 concurrent virtual users
    duration: '10s', // Sustain the attack for 10 seconds
};

export default function () {
    const url = 'http://localhost:8080/submissions';
    
    // Using backticks allows multi-line strings in JavaScript
    const payload = JSON.stringify({
        problemId: 1,
        language: 'JAVA',
        code: `import java.util.HashMap;
import java.util.Map;
class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> numMap = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            if (numMap.containsKey(complement)) {
                return new int[] { numMap.get(complement), i };
            }
            
            numMap.put(nums[i], i);
        }
        
        throw new IllegalArgumentException("No valid pair found");
    }
}`
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYW4iLCJ1c2VySWQiOjI2LCJyb2xlIjoiQURNSU4iLCJvcmdJZCI6MjYsImlhdCI6MTc5MDE4NTEzNSwiZXhwIjoxNzkwMjcxNTM1fQ.7Wrcx5y0GqH73chkPJXMYjPy6G7Wh2UyhS4Tbkm3rCU' // Ensure this is a real token
        },
    };

    const res = http.post(url, payload, params);
    
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}