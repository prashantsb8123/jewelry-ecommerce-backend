-- Production cleanup:
-- 1) Remove all dummy/seed data (V2) and any data created since, so the store
--    starts completely empty. `roles` is intentionally left untouched — it is
--    self-healing (see AdminInitializerOnStartup) and re-seeding it is safe/idempotent.
-- 2) Drop tables that were created by V1 but never backed by any JPA entity
--    (dead schema: no code anywhere reads or writes them).

TRUNCATE TABLE
    order_items,
    cart_items,
    wishlists,
    reviews,
    product_images,
    orders,
    carts,
    products,
    coupons,
    categories,
    users
RESTART IDENTITY CASCADE;

DROP TABLE IF EXISTS payments CASCADE;
DROP TABLE IF EXISTS user_addresses CASCADE;
