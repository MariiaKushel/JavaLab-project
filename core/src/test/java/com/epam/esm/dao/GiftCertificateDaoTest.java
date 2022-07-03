package com.epam.esm.dao;

import com.epam.esm.dao.entity.GiftCertificate;
import com.epam.esm.dao.entity.GiftCertificate_;
import com.epam.esm.enumeration.SearchParameterName;
import com.epam.esm.specification.SpecificationCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class GiftCertificateDaoTest {

    @Autowired
    private GiftCertificateDao dao;
    @Autowired
    private CustomTagDao tagDao;

    public static Object[][] findByIdAndActiveDataProvider() {
        return new Object[][]{
                {1L, true, true},
                {1L, false, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByIdAndActiveDataProvider")
    void findByIdAndActive(long id, boolean active, boolean expected) {
        Optional<GiftCertificate> certificate = dao.findByIdAndActive(id, active);
        boolean actual = certificate.isPresent();
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] findAllByActiveDataProvider() {
        Pageable paging = PageRequest.of(0, 5);
        return new Object[][]{
                {true, paging, 5},
                {false, paging, 0}
        };
    }

    @ParameterizedTest
    @MethodSource("findAllByActiveDataProvider")
    void findAllByActive(boolean active, Pageable paging, int expected) {
        List<GiftCertificate> certificates = dao.findAllByActive(active, paging).toList();
        int actual = certificates.size();
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] countByActiveDataProvider() {
        return new Object[][]{
                {true, 10000},
                {false, 0}
        };
    }

    @ParameterizedTest
    @MethodSource("countByActiveDataProvider")
    void countByActive(boolean active, int expected) {
        int actual = (int) dao.countByActive(active);
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] findAllDataProvider() {
        Sort sorting = Sort.by(Sort.Direction.DESC, GiftCertificate_.CREATE_DATE);
        Pageable paging = PageRequest.of(0, 5, sorting);

        Map<SearchParameterName, String> param1 = new HashMap<>();
        param1.put(SearchParameterName.TAG, "tag_1");
        param1.put(SearchParameterName.NAME, "01");
        param1.put(SearchParameterName.DESCRIPTION, "01");
        param1.put(SearchParameterName.ACTIVE, "true");
        Specification<GiftCertificate> specification1 = SpecificationCreator.getSpecification(param1);

        Map<SearchParameterName, String> param2 = new HashMap<>();
        param2.put(SearchParameterName.TAG, "tag_1");
        param2.put(SearchParameterName.ACTIVE, "true");
        Specification<GiftCertificate> specification2 = SpecificationCreator.getSpecification(param2);

        Map<SearchParameterName, String> param3 = new HashMap<>();
        param3.put(SearchParameterName.ACTIVE, "false");
        Specification<GiftCertificate> specification3 = SpecificationCreator.getSpecification(param3);

        Map<SearchParameterName, String> param4 = new HashMap<>();
        param4.put(SearchParameterName.ACTIVE, "true");
        Specification<GiftCertificate> specification4 = SpecificationCreator.getSpecification(param4);
        return new Object[][]{
                {specification1, paging, 5},
                {specification2, paging, 5},
                {specification3, paging, 0},
                {specification4, paging, 5},
        };
    }

    @ParameterizedTest
    @MethodSource("findAllDataProvider")
    void findAll(Specification<GiftCertificate> specification, Pageable paging, int expected) {
        List<GiftCertificate> certificates = dao.findAll(specification, paging).toList();
        int actual = certificates.size();
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] countDataProvider() {
        Map<SearchParameterName, String> param1 = new HashMap<>();
        param1.put(SearchParameterName.TAG, "tag_1");
        param1.put(SearchParameterName.NAME, "001");
        param1.put(SearchParameterName.DESCRIPTION, "001");
        param1.put(SearchParameterName.ACTIVE, "true");
        Specification<GiftCertificate> specification1 = SpecificationCreator.getSpecification(param1);

        Map<SearchParameterName, String> param2 = new HashMap<>();
        param2.put(SearchParameterName.TAG, "tag_1000");
        param2.put(SearchParameterName.ACTIVE, "true");
        Specification<GiftCertificate> specification2 = SpecificationCreator.getSpecification(param2);

        Map<SearchParameterName, String> param3 = new HashMap<>();
        param3.put(SearchParameterName.ACTIVE, "false");
        Specification<GiftCertificate> specification3 = SpecificationCreator.getSpecification(param3);

        return new Object[][]{
                {specification1, 9L},
                {specification2, 30L},
                {specification3, 0L}
        };
    }

    @ParameterizedTest
    @MethodSource("countDataProvider")
    void count(Specification<GiftCertificate> specification, long expected) {
        long actual = dao.count(specification);
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] findAllByTagsNamesAndActiveDataProvider() {
        Pageable paging = PageRequest.of(0, 10);
        String[] tagsNames1 = {"tag_999", "tag_1000"};
        String[] tagsNames2 = {"tag_998", "tag_999", "tag_1000"};
        String[] tagsNames3 = {"tag_5", "tag_1000"};
        return new Object[][]{
                {tagsNames1, true, paging, 10},
                {tagsNames1, false, paging, 0},
                {tagsNames2, true, paging, 10},
                {tagsNames3, true, paging, 0}
        };
    }

    @ParameterizedTest
    @MethodSource("findAllByTagsNamesAndActiveDataProvider")
    void findAllByTagsNamesAndActive(String[] tagsNames, boolean active, Pageable paging, int expected) {
        List<GiftCertificate> certificates = dao.findAllByTagsNamesAndActive(tagsNames, active, paging);
        int actual = certificates.size();
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] countByTagsNamesAndActiveDataProvider() {
        String[] tagsNames1 = {"tag_999", "tag_1000"};
        String[] tagsNames2 = {"tag_998", "tag_999", "tag_1000"};
        String[] tagsNames3 = {"tag_5", "tag_1000"};
        return new Object[][]{
                {tagsNames1, true, 20},
                {tagsNames1, false, 0},
                {tagsNames2, true, 10},
                {tagsNames3, true, 0}
        };
    }

    @ParameterizedTest
    @MethodSource("countByTagsNamesAndActiveDataProvider")
    void countByTagsNamesAndActive(String[] tagsNames, boolean active, int expected) {
        int actual = dao.countByTagsNamesAndActive(tagsNames, active);
        Assertions.assertEquals(expected, actual);
    }

    public static Object[][] findByNameAndDescriptionAndPriceAndDurationAndActiveDataProvider() {
        return new Object[][]{
                {"certificate 1", "description 1", new BigDecimal("110"), 60, true, true},
                {"certificate A", "description 1", new BigDecimal("110"), 60, true, false},
                {"certificate 1", "description A", new BigDecimal("110"), 60, true, false},
                {"certificate 1", "description 1", new BigDecimal("111"), 60, true, false},
                {"certificate 1", "description 1", new BigDecimal("110"), 61, true, false},
                {"certificate 1", "description 1", new BigDecimal("110"), 60, false, false}
        };
    }

    @ParameterizedTest
    @MethodSource("findByNameAndDescriptionAndPriceAndDurationAndActiveDataProvider")
    void findByNameAndDescriptionAndPriceAndDurationAndActive(String name,
                                                              String description,
                                                              BigDecimal price,
                                                              int duration,
                                                              boolean active,
                                                              boolean expected) {
        Optional<GiftCertificate> certificate =
                dao.findByNameAndDescriptionAndPriceAndDurationAndActive(name, description, price, duration, active);
        boolean actual = certificate.isPresent();
        Assertions.assertEquals(expected, actual);
    }
}