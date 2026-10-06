package ai.fin.repository;

import ai.fin.dto.dashboard.CategoryBreakdownItem;
import ai.fin.dto.dashboard.DailyTrendItem;
import ai.fin.dto.dashboard.TopExpenseItem;
import ai.fin.entities.Transaction;
import ai.fin.entities.User;
import ai.fin.enums.TxnType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    void deleteAllByUser_Id(String userId);

    @Query(
            value = """
                    select t from Transaction t
                    left join fetch t.category c
                    left join fetch t.paymentMode pm
                    where t.user = :user
                      and (:type is null or t.type = :type)
                      and (:categoryId is null or c.id = :categoryId)
                      and t.date >= coalesce(:dateFrom, t.date)
                      and t.date <= coalesce(:dateTo, t.date)
                    """,
            countQuery = """
                    select count(t) from Transaction t
                    left join t.category c
                    where t.user = :user
                      and (:type is null or t.type = :type)
                      and (:categoryId is null or c.id = :categoryId)
                      and t.date >= coalesce(:dateFrom, t.date)
                      and t.date <= coalesce(:dateTo, t.date)
                    """
    )
    Page<Transaction> findAllByFilter(@Param("user") User user,
                                      @Param("type") TxnType type,
                                      @Param("categoryId") String categoryId,
                                      @Param("dateFrom") LocalDate dateFrom,
                                      @Param("dateTo") LocalDate dateTo, Pageable pageable
    );

    @EntityGraph(attributePaths = {"category", "paymentMode"})
    Optional<Transaction> findByIdAndUser_Id(String id, String userId);

    @EntityGraph(attributePaths = {"category", "paymentMode"})
    List<Transaction> findByUserAndDate(User user, LocalDate date);

    @EntityGraph(attributePaths = {"category", "paymentMode"})
    List<Transaction> findAllByUser_IdOrderByDateDesc(String userId);

    @EntityGraph(attributePaths = {"category", "paymentMode"})
    Slice<Transaction> findByUser_Id(String userId, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "paymentMode"})
    List<Transaction> findAllByUserAndDateOrderByDateDesc(User user, LocalDate date);

    @Query("""
                select coalesce(sum(t.amount), 0)
                from Transaction t
                where t.user.id     = :userId
                  AND t.category.id = :categoryId
                  AND t.type        = ai.fin.enums.TxnType.EXPENSE
                  AND t.date >= :startDate
                  AND t.date <= :endDate
            """)
    BigDecimal sumExpensesByCategoryAndMonth(
            @Param("userId") String userId,
            @Param("categoryId") String categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
            select coalesce(sum(t.amount), 0)
            from Transaction t
            where t.user.id = :userId
              and t.type = :type
              and t.date between :from and :to
            """)
    BigDecimal sumByUserAndTypeAndDateBetween(
            @Param("userId") String userId,
            @Param("type") TxnType type,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );


    @Query("""
            select new ai.fin.dto.dashboard.CategoryBreakdownItem(
                c.name,
                sum(t.amount)
            )
            from Transaction t
            JOIN t.category c
            where t.user.id = :userId
              AND t.type = :type
              AND t.date BETWEEN :from AND :to
            GROUP BY c.name
            order by sum(t.amount) desc
            """)
    List<CategoryBreakdownItem> findCategoryBreakdown(@Param("userId") String userId, @Param("type") TxnType type,
                                                      @Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
            select new ai.fin.dto.dashboard.DailyTrendItem(
                t.date,
                sum(case when t.type = ai.fin.enums.TxnType.INCOME  then t.amount else 0 end),
                sum(case when t.type = ai.fin.enums.TxnType.EXPENSE then t.amount else 0 end)
            )
            from Transaction t
            where t.user.id = :userId
              AND t.date BETWEEN :from AND :to
            GROUP BY t.date
            order by t.date ASC
            """)
    List<DailyTrendItem> findDailyTrend(@Param("userId") String userId, @Param("from") LocalDate from,
                                        @Param("to") LocalDate to);

    @Query("""
            SELECT new ai.fin.dto.dashboard.TopExpenseItem(
                t.id, t.amount, c.name, t.note, t.date)
            FROM Transaction t
            LEFT JOIN t.category c
            WHERE t.user.id = :userId
              AND t.type = ai.fin.enums.TxnType.EXPENSE
              AND t.date BETWEEN :from AND :to
            ORDER BY t.amount DESC
            LIMIT 5
            """)
    List<TopExpenseItem> findTop5Expenses(@Param("userId") String userId, @Param("from") LocalDate from,
                                          @Param("to") LocalDate to);
}
