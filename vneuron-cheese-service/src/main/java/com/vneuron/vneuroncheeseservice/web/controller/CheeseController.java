package com.vneuron.vneuroncheeseservice.web.controller;

import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.creamery.model.CheesePagedList;
import com.vneuron.creamery.model.CheeseStyleEnum;
import com.vneuron.vneuroncheeseservice.services.CheeseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/v1/")
@RestController
public class CheeseController {

    private static final Integer DEFAULT_PAGE_NUMBER = 0;
    private static final Integer DEFAULT_PAGE_SIZE = 25;

    private final CheeseService cheeseService;

    @GetMapping(produces = { "application/json" }, path = "cheese")
    public ResponseEntity<CheesePagedList> listCheeses(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                   @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                   @RequestParam(value = "cheeseName", required = false) String cheeseName,
                                                   @RequestParam(value = "cheeseStyle", required = false) CheeseStyleEnum cheeseStyle,
                                                   @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){

        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        if (pageNumber == null || pageNumber < 0){
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        CheesePagedList cheeseList = cheeseService.listCheeses(cheeseName, cheeseStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);

        return new ResponseEntity<>(cheeseList, HttpStatus.OK);
    }

    @GetMapping("cheese/{cheeseId}")
    public ResponseEntity<CheeseDto> getCheeseById(@PathVariable("cheeseId") UUID cheeseId,
                                               @RequestParam(value = "showInventoryOnHand", required = false) Boolean showInventoryOnHand){
        if (showInventoryOnHand == null) {
            showInventoryOnHand = false;
        }

        return new ResponseEntity<>(cheeseService.getById(cheeseId, showInventoryOnHand), HttpStatus.OK);
    }

    @GetMapping("cheeseUpc/{upc}")
    public ResponseEntity<CheeseDto> getCheeseByUpc(@PathVariable("upc") String upc){
        return new ResponseEntity<>(cheeseService.getByUpc(upc), HttpStatus.OK);
    }

    @PostMapping(path = "cheese")
    public ResponseEntity saveNewCheese(@RequestBody @Validated CheeseDto cheeseDto){
        return new ResponseEntity<>(cheeseService.saveNewCheese(cheeseDto), HttpStatus.CREATED);
    }

    @PutMapping("cheese/{cheeseId}")
    public ResponseEntity updateCheeseById(@PathVariable("cheeseId") UUID cheeseId, @RequestBody @Validated CheeseDto cheeseDto){
        return new ResponseEntity<>(cheeseService.updateCheese(cheeseId, cheeseDto), HttpStatus.NO_CONTENT);
    }

}
