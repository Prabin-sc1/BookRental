package com.bookrental.bookrental.controller;

import com.bookrental.bookrental.constants.ModuleNameConstants;
import com.bookrental.bookrental.enums.Message;
import com.bookrental.bookrental.generic.GlobalApiResponse;
import com.bookrental.bookrental.pojo.category.CategoryRequestPojo;
import com.bookrental.bookrental.pojo.category.CategoryResponsePojo;
import com.bookrental.bookrental.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/category")
@Tag(name = ModuleNameConstants.CATEGORY)
public class CategoryController extends MyBaseController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
        this.module = ModuleNameConstants.CATEGORY;
    }

    @PostMapping
    @Operation(
            summary = "Create and Update category",
            description = "This end point used to create and update category",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = CategoryRequestPojo.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> createCategory(@Valid @RequestBody CategoryRequestPojo categoryRequestPojo) {
        categoryService.createUpdateCateogory(categoryRequestPojo);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.SAVE.getCode(), module),
                null));
    }

    @GetMapping
    @Operation(
            summary = "Retrieve all categories",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = CategoryResponsePojo.class)))},
                            description = "This end point fetch all categories"
                    )
            }
    )
    public ResponseEntity<GlobalApiResponse> getAllCategory() {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIVE_ALL.getCode(), module), categoryService.getAllCategory()));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by id",
            description = "This end point can be used for getting category by id",
            responses = @ApiResponse(responseCode = "200",
                    content = {
                            @Content(schema = @Schema(implementation = CategoryResponsePojo.class))
                    }
            )
    )
    public ResponseEntity<GlobalApiResponse> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.RETRIEVE.getCode(), module),
                categoryService.getCategoryById(id)));

    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Category",
            description = "This end point is used to delete category",
            responses = @ApiResponse(responseCode = "200"
            )
    )
    public ResponseEntity<GlobalApiResponse> deleteCategoryById(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.DELETE.getCode(), module), null));
    }


    @GetMapping("/download-excel-data")
    @Operation(
            summary = "Retrieve all categories in excel",
            responses = {
                    @ApiResponse(responseCode = "200", content = {@Content
                            (array = @ArraySchema
                                    (schema = @Schema(implementation = CategoryResponsePojo.class)))},
                            description = "This end point fetch all categories"
                    )
            }
    )
    public ResponseEntity<Resource> download() throws IOException {
        String fileName = "category.xlsx";
        ByteArrayInputStream bis = categoryService.getExcelData();
        InputStreamResource file = new InputStreamResource(bis);

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/upload")
    @Operation(
            summary = "Upload category excel data",
            description = "This end point used to upload excel data of category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "success",
                            content = {
                                    @Content(schema = @Schema(implementation = MultipartFile.class))
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<GlobalApiResponse> saveTransaction(@RequestParam("file") MultipartFile multipartFile) {
        categoryService.save(multipartFile);
        return ResponseEntity.ok(successResponse(customMessageSource.get(Message.SAVE.getCode(), module),
                null));
    }
}
