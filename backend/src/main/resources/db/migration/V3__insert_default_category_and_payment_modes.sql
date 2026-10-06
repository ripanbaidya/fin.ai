-- Insert default income categories
INSERT INTO categories (id, created_at, updated_at, name, category_type, user_id)
VALUES (gen_random_uuid(), now(), now(), '💰 Salary', 'INCOME', NULL),
       (gen_random_uuid(), now(), now(), '🎁 Bonus', 'INCOME', NULL),
       (gen_random_uuid(), now(), now(), '💻 Freelance', 'INCOME', NULL),
       (gen_random_uuid(), now(), now(), '📈 Investments', 'INCOME', NULL),
       (gen_random_uuid(), now(), now(), '🏦 Interest', 'INCOME', NULL),
       (gen_random_uuid(), now(), now(), '🪙 Other Income', 'INCOME', NULL);


-- Insert default expense categories
INSERT INTO categories (id, created_at, updated_at, name, category_type, user_id)
VALUES (gen_random_uuid(), now(), now(), '🍔 Food & Dining', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '🛒 Groceries', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '🏠 Rent', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '🚕 Transport', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '🎬 Entertainment', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '💡 Utilities', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '🏥 Healthcare', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '🛍️ Shopping', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '🎓 Education', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '✈️ Travel', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '📱 Subscriptions', 'EXPENSE', NULL),
       (gen_random_uuid(), now(), now(), '📦 Other Expenses', 'EXPENSE', NULL);


-- Insert default payment modes
INSERT INTO payment_modes (id, created_at, updated_at, name, user_id)
VALUES (gen_random_uuid(), now(), now(), '💵 Cash', NULL),
       (gen_random_uuid(), now(), now(), '💳 Credit Card', NULL),
       (gen_random_uuid(), now(), now(), '🏦 Bank Transfer', NULL),
       (gen_random_uuid(), now(), now(), '📱 UPI', NULL),
       (gen_random_uuid(), now(), now(), '🧾 Debit Card', NULL),
       (gen_random_uuid(), now(), now(), '🌐 Net Banking', NULL);