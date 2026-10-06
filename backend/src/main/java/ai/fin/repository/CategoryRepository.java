package ai.fin.repository;

import ai.fin.entities.Category;
import ai.fin.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    void deleteAllByUser_Id(String userId);

    /**
     * Retrieves all categories visible to a user, including system defaults (user is null) and the user's
     * own categories, filtered by type (or all if type is null).
     */
    @Query("select c from Category c " +
            "where (:type is null or c.categoryType = :type) " +
            "and (c.user is null or c.user.id = :userId) " +
            "order by c.name asc")
    List<Category> findAllVisibleToUser(@Param("userId") String userId, @Param("type") CategoryType type);

    /**
     * Checks if a category with the given name and type already exists for the user or as a system default.
     * Used to prevent duplicate category names while creating a new category.
     */
    @Query("select (count(c) > 0) from Category c " +
            "where lower(c.name) = lower(:name) " +
            "and c.categoryType = :categoryType " +
            "and (c.user.id = :userId or c.user is null)")
    boolean isNameTaken(@Param("userId") String userId,
                        @Param("categoryType") CategoryType categoryType,
                        @Param("name") String name);

    /**
     * Checks if another category with the same name and type already exists (excluding the current category ID).
     * Used when updating an existing category.
     */
    @Query("select (count(c) > 0) from Category c " +
            "where lower(c.name) = lower(:name) " +
            "and c.categoryType = :categoryType " +
            "and c.id <> :categoryId " +
            "and (c.user.id = :userId or c.user is null)")
    boolean isNameTakenExcludingId(@Param("userId") String userId,
                                   @Param("categoryType") CategoryType categoryType,
                                   @Param("name") String name,
                                   @Param("categoryId") String categoryId);

    /**
     * Finds a category by ID that belongs to the specified user.
     */
    Optional<Category> findByIdAndUser_Id(String id, String userId);
}
