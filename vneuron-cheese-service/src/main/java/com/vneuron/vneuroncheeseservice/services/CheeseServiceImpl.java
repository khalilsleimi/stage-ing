package com.vneuron.vneuroncheeseservice.services;

import com.vneuron.creamery.model.CheeseDto;
import com.vneuron.creamery.model.CheesePagedList;
import com.vneuron.creamery.model.CheeseStyleEnum;
import com.vneuron.vneuroncheeseservice.domain.Cheese;
import com.vneuron.vneuroncheeseservice.repositories.CheeseRepository;
import com.vneuron.vneuroncheeseservice.web.controller.NotFoundException;
import com.vneuron.vneuroncheeseservice.web.mappers.CheeseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class CheeseServiceImpl implements CheeseService {
    private final CheeseRepository cheeseRepository;
    private final CheeseMapper cheeseMapper;

    @Cacheable(cacheNames = "cheeseListCache", condition = "#showInventoryOnHand == false ")
    @Override
    public CheesePagedList listCheeses(String cheeseName, CheeseStyleEnum cheeseStyle, PageRequest pageRequest, Boolean showInventoryOnHand) {

        CheesePagedList cheesePagedList;
        Page<Cheese> cheesePage;

        if (!StringUtils.isEmpty(cheeseName) && !StringUtils.isEmpty(cheeseStyle)) {
            //search both
            cheesePage = cheeseRepository.findAllByCheeseNameAndCheeseStyle(cheeseName, cheeseStyle, pageRequest);
        } else if (!StringUtils.isEmpty(cheeseName) && StringUtils.isEmpty(cheeseStyle)) {
            //search cheese_service name
            cheesePage = cheeseRepository.findAllByCheeseName(cheeseName, pageRequest);
        } else if (StringUtils.isEmpty(cheeseName) && !StringUtils.isEmpty(cheeseStyle)) {
            //search cheese_service style
            cheesePage = cheeseRepository.findAllByCheeseStyle(cheeseStyle, pageRequest);
        } else {
            cheesePage = cheeseRepository.findAll(pageRequest);
        }

        if (showInventoryOnHand){
            cheesePagedList = new CheesePagedList(cheesePage
                    .getContent()
                    .stream()
                    .map(cheeseMapper::cheeseToCheeseDtoWithInventory)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(cheesePage.getPageable().getPageNumber(),
                                    cheesePage.getPageable().getPageSize()),
                    cheesePage.getTotalElements());
        } else {
            cheesePagedList = new CheesePagedList(cheesePage
                    .getContent()
                    .stream()
                    .map(cheeseMapper::cheeseToCheeseDto)
                    .collect(Collectors.toList()),
                    PageRequest
                            .of(cheesePage.getPageable().getPageNumber(),
                                    cheesePage.getPageable().getPageSize()),
                    cheesePage.getTotalElements());
        }

        return cheesePagedList;
    }

    @Cacheable(cacheNames = "cheeseCache", key = "#cheeseId", condition = "#showInventoryOnHand == false ")
    @Override
    public CheeseDto getById(UUID cheeseId, Boolean showInventoryOnHand) {
        if (showInventoryOnHand) {
            return cheeseMapper.cheeseToCheeseDtoWithInventory(
                    cheeseRepository.findById(cheeseId).orElseThrow(NotFoundException::new)
            );
        } else {
            return cheeseMapper.cheeseToCheeseDto(
                    cheeseRepository.findById(cheeseId).orElseThrow(NotFoundException::new)
            );
        }
    }

    @Override
    public CheeseDto saveNewCheese(CheeseDto cheeseDto) {
        return cheeseMapper.cheeseToCheeseDto(cheeseRepository.save(cheeseMapper.cheeseDtoToCheese(cheeseDto)));
    }

    @Override
    public CheeseDto updateCheese(UUID cheeseId, CheeseDto cheeseDto) {
        Cheese cheese = cheeseRepository.findById(cheeseId).orElseThrow(NotFoundException::new);

        cheese.setCheeseName(cheeseDto.getCheeseName());
        cheese.setCheeseStyle(cheeseDto.getCheeseStyle().name());
        cheese.setPrice(cheeseDto.getPrice());
        cheese.setUpc(cheeseDto.getUpc());

        return cheeseMapper.cheeseToCheeseDto(cheeseRepository.save(cheese));
    }

    @Cacheable(cacheNames = "cheeseUpcCache")
    @Override
    public CheeseDto getByUpc(String upc) {
        return cheeseMapper.cheeseToCheeseDto(cheeseRepository.findByUpc(upc));
    }
}
