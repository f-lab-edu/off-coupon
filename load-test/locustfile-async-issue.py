import random
from locust import task, FastHttpUser, stats

stats.PERCENTILES_TO_CHART = [0.95, 0.99]

class AsyncCouponIssue(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def async_issue(self):
        coupon_id = 1
        member_id = random.randint(1, 100000000)
        # 파라미터로 couponId와 memberId 전달
        params = {"couponId": coupon_id, "memberId": member_id}
        with self.client.request("POST", "/api/v1/event/1/issues-async", params=params) as response:
            pass
