package com.bantar.service;

import com.bantar.dto.ResponseDTO;
import com.bantar.entity.MindReaderCategoryEntity;
import com.bantar.entity.MindReaderEntity;
import com.bantar.repository.MindReaderRepository;
import com.bantar.service.interfaces.QuestionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class MindReaderServiceTest {

    private QuestionService mindReaderService;
    @Mock
    private MindReaderRepository mindReaderRepository;
    @Mock
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        mindReaderService = new MindReaderService(mindReaderRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    private List<MindReaderEntity> createEntities() {
        MindReaderEntity r1 = new MindReaderEntity(1L, "Guess what number I'm thinking of");
        MindReaderEntity r2 = new MindReaderEntity(2L, "What is my favourite season");
        MindReaderEntity r3 = new MindReaderEntity(3L, "What trait do you think I value most?");

        MindReaderCategoryEntity c1 = new MindReaderCategoryEntity(1L, "GENERAL", r1);
        MindReaderCategoryEntity c2 = new MindReaderCategoryEntity(2L, "FUN", r2);
        MindReaderCategoryEntity c3 = new MindReaderCategoryEntity(3L, "INSIGHT", r3);
        MindReaderCategoryEntity c4 = new MindReaderCategoryEntity(4L, "PERSONALITY", r3);

        r1.setCategories(List.of(c1));
        r2.setCategories(List.of(c2));
        r3.setCategories(List.of(c3, c4));

        return List.of(r1, r2, r3);
    }

    @Test
    void testGetById() {
        MindReaderEntity entity = new MindReaderEntity(1L, "Guess what number I'm thinking of");
        List<MindReaderEntity> entities = List.of(entity);

        when(mindReaderRepository.getAll()).thenReturn(entities);

        ResponseDTO<?> dto = mindReaderService.getById(1);

        assertNotNull(dto);
        assertEquals(1, dto.getId());
        assertEquals("Guess what number I'm thinking of", dto.getText());
    }

    @Test
    void testGetAll() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        List<ResponseDTO<?>> list = mindReaderService.getAll();

        assertEquals(3, list.size());
    }

    @Test
    void testGetByRange() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        List<ResponseDTO<?>> list = mindReaderService.getByRange(1, 2);

        assertEquals(2, list.size());
        assertEquals("What is my favourite season", list.get(0).getText());
    }

    @Test
    void testGetByCategory() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        List<ResponseDTO<?>> list = mindReaderService.getByCategory("FUN");

        assertEquals(1, list.size());
        assertEquals("What is my favourite season", list.get(0).getText());
    }

    @Test
    void testGetByCategories() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        List<ResponseDTO<?>> list = mindReaderService.getByCategories(List.of("FUN", "GENERAL"));

        assertEquals(2, list.size());

        List<String> texts = list.stream().map(ResponseDTO::getText).toList();

        assertTrue(texts.contains("What is my favourite season"));
        assertTrue(texts.contains("Guess what number I'm thinking of"));
    }

    @Test
    void testGetByFilteredCategories() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        List<ResponseDTO<?>> list = mindReaderService.getByFilteredCategories(List.of("INSIGHT", "PERSONALITY"));

        assertEquals(1, list.size());
        assertEquals("What trait do you think I value most?", list.get(0).getText());
    }

    @Test
    void testRefresh() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        mindReaderService.refresh();

        List<ResponseDTO<?>> list = mindReaderService.getAll();

        assertEquals(3, list.size());
    }

    @Test
    void testGetByInvalidCategory() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        List<ResponseDTO<?>> list = mindReaderService.getByCategory("INVALID");

        assertTrue(list == null || list.isEmpty());
    }

    @Test
    void testGetByCategoriesAllInvalid() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        List<ResponseDTO<?>> list = mindReaderService.getByCategories(List.of("WRONG", "BAD"));

        assertTrue(list == null || list.isEmpty());
    }

    @Test
    void testGetByFilteredCategoriesInvalid() {
        List<MindReaderEntity> entities = createEntities();
        when(mindReaderRepository.getAll()).thenReturn(entities);

        List<ResponseDTO<?>> list = mindReaderService.getByFilteredCategories(List.of("FUN", "INSIGHT")); // none match both

        assertTrue(list == null || list.isEmpty());
    }
}