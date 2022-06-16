package com.epam.esm;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class TestDataGenerator {

    private static final Path PATH_TO_DATA = Paths.get("src\\test\\resources\\data.sql");

    private static final int num_10000 = 10000;
    private static final int num_1000 = 1000;
    private static final int num_100 = 100;
    private static final int num_10 = 10;
    private static final int num_30 = 30;
    private static final int num_5 = 5;

    private static final String GC_INSERT_STATIC_PART_1 = "INSERT INTO `gift_certificates` (`name`, `description`, `price`, `duration`, `create_date`, `last_update_date`, `active`) VALUES ('certificate ";
    private static final String GC_INSERT_STATIC_PART_2 = "', 'description ";
    private static final String TAG_INSERT_STATIC_PART = "INSERT INTO `tags` (`name`) VALUES ('tag_";
    private static final String GC_TAG_COUPLING_INSERT_STATIC_PART = "INSERT INTO `gift_certificates_tags` (`id_gift_certificate`, `id_tag`) VALUES ('";
    private static final String USER_INSERT_STATIC_PART = "INSERT INTO `users` (`login`, `password`, `name`, `role`) VALUES ('";
    private static final String ORDER_INSERT_STATIC_PART = "INSERT INTO `orders` (`id_user`, `purchase_date`, `amount`) VALUES ('";
    private static final String ORDER_GC_COUPLING_INSERT_STATIC_PART = "INSERT INTO `orders_gift_certificates` (`id_order`, `id_gift_certificate`) VALUES ('";

    private static final String COMMA = "', '";
    private static final String COMMA_BRACKET = "');";

    @Test
    public void generateData() throws IOException {
        generateTags(PATH_TO_DATA);
        generateGiftCertificates(PATH_TO_DATA, StandardOpenOption.APPEND);
        generateGiftCertificateTagCoupling(PATH_TO_DATA, StandardOpenOption.APPEND);
        generateUsers(PATH_TO_DATA, StandardOpenOption.APPEND);
        Map<Long, BigDecimal> amountByOrders = generateOrders(PATH_TO_DATA, StandardOpenOption.APPEND);
        generateOrderGiftCertificateCoupling(amountByOrders, PATH_TO_DATA, StandardOpenOption.APPEND);
    }

    private void generateGiftCertificates(Path path, StandardOpenOption... option) throws IOException {
        List<String> giftCertificates = new ArrayList<>();
        LocalDateTime date = LocalDateTime.parse("2022-02-01T11:59:01");

        for (int i = 1; i <= num_10000; i++) {
            StringBuilder sb = new StringBuilder();

            sb.append(GC_INSERT_STATIC_PART_1); // insert + 'certificate'
            sb.append(i); // number part of certificate
            sb.append(GC_INSERT_STATIC_PART_2); //'description'
            sb.append(i); // number part of description
            sb.append(COMMA);

            int pk = i % num_10;
            BigDecimal price = new BigDecimal(num_100 + pk * num_10);
            sb.append(price); //'price'
            sb.append(COMMA);

            int dk = i % num_5;
            int duration = num_30 * (dk + 1);
            sb.append(duration); //'duration'
            sb.append(COMMA);
            date = date.plusMinutes(1);
            sb.append(date); // 'create date'
            sb.append(COMMA);
            sb.append(date); // 'last update date'
            sb.append(COMMA);
            sb.append(1); // 'active'
            sb.append(COMMA_BRACKET);

            giftCertificates.add(sb.toString());
        }
        Files.write(path, giftCertificates, option);
    }

    private void generateTags(Path path, StandardOpenOption... option) throws IOException {
        List<String> tags = new ArrayList<>();

        for (int i = 1; i <= num_1000; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(TAG_INSERT_STATIC_PART);
            sb.append(i);
            sb.append(COMMA_BRACKET);

            tags.add(sb.toString());
        }
        Files.write(path, tags, option);
    }

    private void generateGiftCertificateTagCoupling(Path path, StandardOpenOption... option) throws IOException {
        List<String> couplings = new ArrayList<>();

        for (int i = 1; i <= num_10000; i++) {
            Set<Long> tagsId = new HashSet<>();
            long firstTag = i % num_1000 == 0
                    ? num_1000
                    : (long) i % num_1000;
            tagsId.add(firstTag);
            if (i % num_1000 != 0) {
                long secondTag = firstTag + 1;
                tagsId.add(secondTag);
                if (secondTag != num_1000) {
                    tagsId.add(secondTag + 1);
                }
            }

            while (tagsId.size() > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(GC_TAG_COUPLING_INSERT_STATIC_PART);
                sb.append(i);
                sb.append(COMMA);
                Long tagId = tagsId.stream().findFirst().get();
                tagsId.remove(tagId);
                sb.append(tagId);
                sb.append(COMMA_BRACKET);

                couplings.add(sb.toString());
            }
        }
        Files.write(path, couplings, option);
    }

    private void generateUsers(Path path, StandardOpenOption... option) throws IOException {
        List<String> users = new ArrayList<>();
        Random random = new Random();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        List<String> names = new ArrayList<>();
        names.add("Vasya");
        names.add("Petr");
        names.add("Alise");
        names.add("Hanna");
        names.add("Ivan");
        names.add("Gregor");
        names.add("Olha");
        names.add("Jim");
        names.add("Lena");
        names.add("Mick");
        names.add("Mary");

        for (int i = 1; i <= num_1000 + 1; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(USER_INSERT_STATIC_PART);
            sb.append(i != 1001 ? i : "admin");
            sb.append("@gmail.com");
            sb.append(COMMA);
            sb.append("{bcrypt}");
            sb.append(encoder.encode(String.valueOf(i != 1001 ? i : "admin")));
            sb.append(COMMA);

            int nameIndex = random.nextInt(names.size());
            String name = names.get(nameIndex);
            sb.append(name);
            sb.append("_");
            sb.append(i);
            sb.append(COMMA);
            sb.append(i != 1001 ? "ROLE_USER" : "ROLE_ADMIN");
            sb.append(COMMA_BRACKET);

            users.add(sb.toString());
        }

        Files.write(path, users, option);
    }

    private Map<Long, BigDecimal> generateOrders(Path path, StandardOpenOption... option) throws IOException {
        List<String> orders = new ArrayList<>();
        LocalDateTime date = LocalDateTime.parse("2022-03-01T09:00:01");
        Random random = new Random();

        Map<Long, Integer> numberOfOrdersByUser = new HashMap<>();
        for (long userId = 1; userId <= num_1000; userId++) {
            int numberOfOrders = random.nextInt(num_5) + num_5;
            numberOfOrdersByUser.put(userId, numberOfOrders);
        }

        Map<Long, BigDecimal> amountByOrders = new HashMap<>();
        Long orderId = 1L;
        while (!numberOfOrdersByUser.isEmpty()) {
            Long userId = random.nextLong(num_1000) + 1;
            Integer numOfOrders = numberOfOrdersByUser.get(userId);
            if (numOfOrders == null) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ORDER_INSERT_STATIC_PART);
            sb.append(userId);
            sb.append(COMMA);
            date = date.plusMinutes(2);
            sb.append(date);
            sb.append(COMMA);
            BigDecimal amount = new BigDecimal(random.nextInt(400) + num_100);
            amountByOrders.put(orderId, amount);
            orderId++;
            sb.append(amount);
            sb.append(COMMA_BRACKET);
            orders.add(sb.toString());

            numOfOrders--;
            if (numOfOrders == 0) {
                numberOfOrdersByUser.remove(userId);
            } else {
                numberOfOrdersByUser.put(userId, numOfOrders);
            }

        }
        Files.write(path, orders, option);
        return amountByOrders;
    }

    private void generateOrderGiftCertificateCoupling(Map<Long, BigDecimal> amountByOrders, Path path, StandardOpenOption... option) throws IOException {
        List<String> couplings = new ArrayList<>();
        Random random = new Random();

        for (Long orderId = 1L; orderId <= amountByOrders.size(); orderId++) {
            BigDecimal amount = amountByOrders.get(orderId);
            int amountAsInt = Integer.parseInt(amount.toString());
            int hundred = amountAsInt / num_100;
            for (int j = 0; j < hundred; j++) {
                StringBuilder sb = new StringBuilder();
                sb.append(ORDER_GC_COUPLING_INSERT_STATIC_PART);
                sb.append(orderId);
                sb.append(COMMA);
                Long giftCertificateId = random.nextLong(num_10000) + 1;
                sb.append(giftCertificateId);
                sb.append(COMMA_BRACKET);

                couplings.add(sb.toString());
            }
        }
        Files.write(path, couplings, option);
    }
}