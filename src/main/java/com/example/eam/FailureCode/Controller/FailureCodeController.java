// package com.example.eam.FailureCode.Controller;

// import com.example.eam.Common.ApiResponse;
// import com.example.eam.FailureCode.Dto.*;
// import com.example.eam.FailureCode.Service.FailureCodeService;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/failure-codes")
// @RequiredArgsConstructor
// public class FailureCodeController {

//     private final FailureCodeService failureCodeService;

//     @PostMapping
//     public ResponseEntity<ApiResponse<FailureCodeResponseDto>> create(
//             @Valid @RequestBody FailureCodeCreateDto dto) {
//         FailureCodeResponseDto response = failureCodeService.create(dto);
//         ApiResponse<FailureCodeResponseDto> apiResponse = ApiResponse.successResponse(
//                 HttpStatus.CREATED.value(),
//                 "Failure code created successfully",
//                 response
//         );
//         return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ApiResponse<FailureCodeResponseDto>> get(@PathVariable Long id) {
//         FailureCodeResponseDto response = failureCodeService.get(id);
//         ApiResponse<FailureCodeResponseDto> apiResponse = ApiResponse.successResponse(
//                 HttpStatus.OK.value(),
//                 "Failure code retrieved successfully",
//                 response
//         );
//         return ResponseEntity.ok(apiResponse);
//     }

//     @GetMapping
//     public ResponseEntity<ApiResponse<List<FailureCodeResponseDto>>> list() {
//         List<FailureCodeResponseDto> response = failureCodeService.list();
//         ApiResponse<List<FailureCodeResponseDto>> apiResponse = ApiResponse.successResponse(
//                 HttpStatus.OK.value(),
//                 response.isEmpty() ? "No failure codes found" : "Failure codes retrieved successfully",
//                 response
//         );
//         return ResponseEntity.ok(apiResponse);
//     }

//     @PatchMapping("/{id}")
//     public ResponseEntity<ApiResponse<FailureCodeResponseDto>> update(
//             @PathVariable Long id,
//             @Valid @RequestBody FailureCodeUpdateDto dto) {
//         FailureCodeResponseDto response = failureCodeService.update(id, dto);
//         ApiResponse<FailureCodeResponseDto> apiResponse = ApiResponse.successResponse(
//                 HttpStatus.OK.value(),
//                 "Failure code updated successfully",
//                 response
//         );
//         return ResponseEntity.ok(apiResponse);
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
//         failureCodeService.delete(id);
//         ApiResponse<Void> apiResponse = ApiResponse.successResponse(
//                 HttpStatus.NO_CONTENT.value(),
//                 "Failure code deleted successfully",
//                 null
//         );
//         return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
//     }
// }