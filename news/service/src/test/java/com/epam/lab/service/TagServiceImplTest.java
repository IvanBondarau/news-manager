package com.epam.lab.service;

import com.epam.lab.dao.TagDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(JUnit4.class)
public class TagServiceImplTest {

    @Mock
    private TagDao tagDao;

    private TagService service;

    @Before
    public void init() {
        service = new TagServiceImpl(tagDao);
    }

    @Test
    public void createNewTagValid() {

    }

}