package com.doubleA.user;

import com.doubleA.crypto.filter.FilterBuilderService;
import com.doubleA.crypto.filter.FilterCondition;
import com.doubleA.crypto.filter.GenericFilterCriteriaBuilder;
import com.doubleA.crypto.filter.PageResponse;
import com.doubleA.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final FilterBuilderService filterBuilderService;

    public UserController(UserRepository userRepository, UserService userService, FilterBuilderService filterBuilderService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.filterBuilderService = filterBuilderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        log.info(id);
        log.info("[Get] Request to method 'getUserById'");
        try {
            return ResponseEntity.ok(UserDTO.from(userRepository.findById(id).orElseThrow()));
        } catch (Exception e) {
            log.error("Error in method 'getAdvertisementsByUser': " + e.getMessage());
            return ResponseEntity.badRequest().body("The user with id: [" + id + "] " + "doesn't exist");
        }
    }

    @GetMapping("/page")
    public ResponseEntity<?> getSearchCriteriaPage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "filterOr", required = false) String filterOr,
            @RequestParam(value = "filterAnd", required = false) String filterAnd,
            @RequestParam(value = "orders", required = false) String orders) {

        log.info("[Get][UserController] Request to method 'getSearchCriteriaPage'");
        try {
            PageResponse<User> response = new PageResponse<>();

            Pageable pageable = filterBuilderService.getPageable(size, page, orders);
            GenericFilterCriteriaBuilder filterCriteriaBuilder = new GenericFilterCriteriaBuilder();

            List<FilterCondition> andConditions = filterBuilderService.createFilterCondition(filterAnd);
            List<FilterCondition> orConditions = filterBuilderService.createFilterCondition(filterOr);

            Query query = filterCriteriaBuilder.addCondition(andConditions, orConditions);
            Page<User> pg = userService.getPage(query, pageable);
            response.setPageStats(pg, pg.getContent());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error in method 'getSearchCriteriaPage': " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

}