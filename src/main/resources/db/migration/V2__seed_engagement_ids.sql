INSERT INTO "engagement-mgmt-sch"."engagements-tbl" (id)
SELECT gen_random_uuid()
FROM generate_series(1, 1000);
