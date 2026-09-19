-- Flyway V2__seed_data.sql
-- Seed data for JYOSHIKA MILLENNIUM

-- Seed Categories: exactly four admin-manageable categories
INSERT INTO categories (id, name, slug, description, is_featured) VALUES
('c1111111-1111-1111-1111-111111111111', 'Necklace', 'necklace', 'Elegant necklaces and pendant sets for every occasion', true),
('c2222222-2222-2222-2222-222222222222', 'Ring', 'ring', 'Rings crafted for engagements, weddings, and everyday elegance', true),
('c3333333-3333-3333-3333-333333333333', 'Bangles', 'bangles', 'Traditional and contemporary bangles & bracelets', true),
('c4444444-4444-4444-4444-444444444444', 'Other', 'other', 'Earrings, anklets, mangalsutras and other fine jewellery pieces', true);

-- Seed Coupons
INSERT INTO coupons (code, discount_percentage, max_discount, min_order_value, expiry_date, is_active) VALUES
('SAVE10', 10.00, 2000.00, 999.00, '2030-12-31 23:59:59+00', true),
('WELCOME15', 15.00, 5000.00, 4999.00, '2030-12-31 23:59:59+00', true);

-- Seed Products (3 per category)
INSERT INTO products (
    id, title, slug, sku, description, category_id, price, sale_price,
    rating, review_count, stock_quantity, is_best_seller, is_new_arrival, is_featured
) VALUES
-- Necklace
('d1111111-1111-1111-1111-111111111111', 'Royal Kundan Gold Necklace Set', 'royal-kundan-gold-necklace-set', 'NCK-001', 'A majestic gold necklace set adorned with intricate Kundan work and cultured pearls.', 'c1111111-1111-1111-1111-111111111111', 242000.00, 219000.00, 4.9, 24, 8, true, true, true),
('d1111111-1111-1111-1111-111111111112', 'Classic Pearl Drop Necklace', 'classic-pearl-drop-necklace', 'NCK-002', 'Timeless freshwater pearl necklace with a delicate gold chain.', 'c1111111-1111-1111-1111-111111111111', 18500.00, NULL, 4.7, 12, 15, false, true, false),
('d1111111-1111-1111-1111-111111111113', 'Temple Coin Choker Necklace', 'temple-coin-choker-necklace', 'NCK-003', 'Antique-finish temple jewellery inspired choker with coin motifs.', 'c1111111-1111-1111-1111-111111111111', 32500.00, 28900.00, 4.8, 19, 10, true, false, true),
-- Ring
('d2222222-2222-2222-2222-222222222221', 'Royal Solitaire Diamond Ring', 'royal-solitaire-diamond-ring', 'RNG-001', 'Exquisite yellow gold solitaire ring featuring a brilliant-cut diamond.', 'c2222222-2222-2222-2222-222222222222', 78500.00, 71500.00, 4.9, 18, 12, true, false, true),
('d2222222-2222-2222-2222-222222222222', 'Minimal Everyday Gold Band', 'minimal-everyday-gold-band', 'RNG-002', 'Sleek, comfortable gold band designed for daily wear.', 'c2222222-2222-2222-2222-222222222222', 9800.00, NULL, 4.5, 7, 25, false, true, false),
('d2222222-2222-2222-2222-222222222223', 'Halo Sapphire Engagement Ring', 'halo-sapphire-engagement-ring', 'RNG-003', 'Blue sapphire centerstone surrounded by a dazzling diamond halo.', 'c2222222-2222-2222-2222-222222222222', 64500.00, 58900.00, 4.8, 14, 6, true, true, true),
-- Bangles
('d3333333-3333-3333-3333-333333333331', 'Hallmarked Gold Kada Bangle', 'hallmarked-gold-kada-bangle', 'BNG-001', 'BIS hallmarked traditional gold kada with an engraved finish.', 'c3333333-3333-3333-3333-333333333333', 55500.00, NULL, 4.7, 21, 9, true, false, true),
('d3333333-3333-3333-3333-333333333332', 'Contemporary Diamond Bracelet', 'contemporary-diamond-bracelet', 'BNG-002', 'Modern tennis bracelet lined with brilliant-cut diamonds.', 'c3333333-3333-3333-3333-333333333333', 112000.00, 99500.00, 4.9, 16, 5, false, true, true),
('d3333333-3333-3333-3333-333333333333', 'Set of 4 Slim Gold Bangles', 'set-of-4-slim-gold-bangles', 'BNG-003', 'Stackable set of four slim gold bangles for a layered look.', 'c3333333-3333-3333-3333-333333333333', 47500.00, 42900.00, 4.6, 9, 11, false, false, false),
-- Other
('d4444444-4444-4444-4444-444444444441', 'Jhumka Gold Drop Earrings', 'jhumka-gold-drop-earrings', 'OTH-001', 'Traditional jhumka earrings with fine filigree detailing.', 'c4444444-4444-4444-4444-444444444444', 21500.00, NULL, 4.8, 22, 14, true, false, true),
('d4444444-4444-4444-4444-444444444442', 'Diamond Mangalsutra Pendant', 'diamond-mangalsutra-pendant', 'OTH-002', 'Auspicious diamond and gold mangalsutra pendant on a black bead chain.', 'c4444444-4444-4444-4444-444444444444', 38900.00, 34900.00, 4.9, 27, 7, true, true, true),
('d4444444-4444-4444-4444-444444444443', 'Gold Anklet Pair', 'gold-anklet-pair', 'OTH-003', 'Lightweight pair of gold anklets with a subtle bell charm.', 'c4444444-4444-4444-4444-444444444444', 15800.00, NULL, 4.5, 6, 20, false, true, false);

-- Seed Product Images
INSERT INTO product_images (product_id, image_url, display_order, is_primary) VALUES
('d1111111-1111-1111-1111-111111111111', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&q=80&w=800', 1, true),
('d1111111-1111-1111-1111-111111111112', 'https://images.unsplash.com/photo-1602752250015-52934bc45613?auto=format&fit=crop&q=80&w=800', 1, true),
('d1111111-1111-1111-1111-111111111113', 'https://images.unsplash.com/photo-1611591437281-460bfbe1220a?auto=format&fit=crop&q=80&w=800', 1, true),
('d2222222-2222-2222-2222-222222222221', 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?auto=format&fit=crop&q=80&w=800', 1, true),
('d2222222-2222-2222-2222-222222222222', 'https://images.unsplash.com/photo-1603561591411-07134e71a2a9?auto=format&fit=crop&q=80&w=800', 1, true),
('d2222222-2222-2222-2222-222222222223', 'https://images.unsplash.com/photo-1614703478004-3928372a0177?auto=format&fit=crop&q=80&w=800', 1, true),
('d3333333-3333-3333-3333-333333333331', 'https://images.unsplash.com/photo-1611591437281-460bfbe1220a?auto=format&fit=crop&q=80&w=800', 1, true),
('d3333333-3333-3333-3333-333333333332', 'https://images.unsplash.com/photo-1599643477877-530eb83abc8e?auto=format&fit=crop&q=80&w=800', 1, true),
('d3333333-3333-3333-3333-333333333333', 'https://images.unsplash.com/photo-1611955167811-4711904bb9f8?auto=format&fit=crop&q=80&w=800', 1, true),
('d4444444-4444-4444-4444-444444444441', 'https://images.unsplash.com/photo-1630019852942-f89202989a59?auto=format&fit=crop&q=80&w=800', 1, true),
('d4444444-4444-4444-4444-444444444442', 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?auto=format&fit=crop&q=80&w=800', 1, true),
('d4444444-4444-4444-4444-444444444443', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?auto=format&fit=crop&q=80&w=800', 1, true);
