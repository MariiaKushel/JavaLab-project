package com.epam.esm.pagination;

import com.epam.esm.exception.CustomErrorCode;
import com.epam.esm.exception.CustomException;

/**
 * Class helps to do pagination operation.
 */
public class Pagination {

    public static final int DEFAULT_MAX_PAGE_SIZE = 50;

    /**
     * Method finds max available page
     *
     * @param size     page size
     * @param quantity total quantity of pagination data
     * @return
     */
    public static int maxPage(int size, long quantity) {
        if (quantity == 0) quantity = 1;
        return quantity % size == 0
                ? (int) (quantity / size)
                : (int) (quantity / size + 1);
    }

    /**
     * Method finds next page
     *
     * @param page     page
     * @param size     page size
     * @param quantity total quantity of pagination data
     * @return next page or the same page if this page is last.
     */
    public static int nextPage(int page, int size, long quantity) {
        int maxPage = maxPage(size, quantity);
        return page < maxPage
                ? page + 1
                : maxPage;
    }

    /**
     * Method finds previous page
     *
     * @param page page
     * @return previous page or the same page if this page is first.
     */
    public static int previousPage(int page) {
        return page > 1
                ? page - 1
                : page;
    }

    /**
     * Method checks pagination parameters
     *
     * @param page     page
     * @param size     page size
     * @param quantity total quantity of pagination data
     * @throws if pagination parameters has not valid value
     */
    public static void check(Integer page, Integer size, long quantity) throws CustomException {
        if (page == null || page <= 0) {
            throw new CustomException("page=" + page, CustomErrorCode.NOT_VALID_DATA);
        }
        if (size == null || size <= 0 || size > Pagination.DEFAULT_MAX_PAGE_SIZE) {
            throw new CustomException("size=" + size, CustomErrorCode.NOT_VALID_DATA);
        }
        int maxPage = maxPage(size, quantity);
        if (page > maxPage) {
            throw new CustomException("page is over max, maxPage=" + maxPage, CustomErrorCode.NOT_VALID_DATA);
        }
    }


    /**
     * Method checks pagination parameters
     *
     * @param page     page as String
     * @param size     page size as String
     * @param quantity total quantity of pagination data
     * @throws if pagination parameters has not valid value
     */
    public static void check(String page, String size, long quantity) throws CustomException {
        try {
            Integer pageAsInt = Integer.parseInt(page);
            Integer sizeAsInt = Integer.parseInt(size);
            check(pageAsInt, sizeAsInt, quantity);
        } catch (NumberFormatException e) {
            throw new CustomException("size=" + size + "; page=" + page, CustomErrorCode.NOT_VALID_DATA);
        }
    }

}
