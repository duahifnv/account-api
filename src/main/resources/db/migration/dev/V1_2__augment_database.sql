insert into users (name, date_of_birth, password)
values
    ('John Smith', '01.01.1990', '$2a$10$xVxWJMxHZh6UKCXhvQIC8.XZn5T1dMBGR.DGm1LoHQUUHY5OqLHiO'), /* password123 */
    ('Jane Doe', '15.06.1985', '$2a$10$NqmHGRx.TKDYEQz0Lc8P9.O6RyQvQqkJ2hPOvEqWxQQ9BBqYCarGy'), /* securepass456 */
    ('Bob Wilson', '30.12.1995', '$2a$10$8KxX5SSLFxTFXOZj0CQ.UedLUk3YFJkqN8bL3aaVIRLBLVm5oZjDi'); /* strongpass789 */

insert into email_data (user_id, email)
values
    (1, 'john.smith@example.com'),
    (2, 'jane.doe@example.com'),
    (3, 'bob.wilson@example.com');

insert into phone_data (user_id, phone)
values
    (1, '79991234567'),
    (1, '79991234568'),
    (2, '79992345678'),
    (3, '79993456789');

insert into accounts (user_id, balance)
values
    (1, 1000.00),
    (2, 2000.00),
    (3, 3000.00);