insert into users (name, date_of_birth, password)
values ('John Smith', TO_DATE('01.01.1990', 'DD.MM.YYYY'), '$2a$12$7VkoGBd1knaKAK5ydb1hDu/Em8lGnhjmvExWglVezM/g6BMFAytN.'), /* john0101 */
       ('Jane Doe', TO_DATE('15.06.1985', 'DD.MM.YYYY'), '$2a$12$QdAlrXwzAbilBQRI0K3RdOsqYTHPsUvuZljprqLCjSq8NxfxpcN/6'), /* jane1506 */
       ('Bob Wilson', TO_DATE('30.12.1995', 'DD.MM.YYYY'), '$2a$12$PHsmKFbWovLgKVUUBASkv.vdnV/MkgqnTXreP1cC8.vTKtTtDAWxe'); /* bob3012 */

insert into email_data (user_id, email)
values (1, 'john.smith@example.com'),
       (2, 'jane.doe@example.com'),
       (3, 'bob.wilson@example.com');

insert into phone_data (user_id, phone)
values (1, '79991234567'),
       (1, '79991234568'),
       (2, '79992345678'),
       (3, '79993456789');

insert into accounts (user_id, balance, max_balance)
values (1, 1000.00, 1000.00 * 2.07),
       (2, 2000.00, 2000.00 * 2.07),
       (3, 3000.00, 3000.00 * 2.07);