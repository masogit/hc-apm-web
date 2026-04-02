-- 添加GEIB调用acct system client
INSERT INTO system_client_info (uid, bind_site_uid, client_key, client_name, client_secret, client_type, create_time,
                                last_access_date)
VALUES (REPLACE(uuid_generate_v4()::text, '-', ''), '2a773154623311e7aeb25254001a1c14',
        '1dee1cec8d7f440dbf26a2d1f21a8580', 'GEIB',
        '$2a$10$F3CSKY43V7DRTGfmtYRDCuTcD1..spTjc/qJ82Foo1AQWixfEaOqK', 'SYNC_DATA', NULL, clock_timestamp());