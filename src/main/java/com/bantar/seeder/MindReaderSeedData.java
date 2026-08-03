package com.bantar.seeder;

import com.bantar.model.MindReaderCategory;
import java.util.List;

final class MindReaderSeedData {

    record Item(String text, List<MindReaderCategory> categories) {
    }

    static final List<Item> ITEMS = List.of(
            new Item("What is my favourite colour?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FAVOURITES)),
            new Item("What colour are my eyes?", List.of(MindReaderCategory.FUN, MindReaderCategory.GENERAL, MindReaderCategory.PHYSICAL)),
            new Item("Can you guess the number I'm thinking of (1-10)?", List.of(MindReaderCategory.FUN, MindReaderCategory.INSIGHT)),
            new Item("If I had to pick a superpower, what would it be?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FUN)),
            new Item("What do you think motivates me the most?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.INSIGHT)),
            new Item("If I could change one thing about my past, what would it be?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.MEMORIES)),
            new Item("Am I more of an introvert or extrovert?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.INSIGHT)),
            new Item("Which trait do I value most in friends?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.INSIGHT)),
            new Item("What is my favourite food?", List.of(MindReaderCategory.FAVOURITES, MindReaderCategory.FOOD_DRINK)),
            new Item("What's my go-to comfort drink?", List.of(MindReaderCategory.FAVOURITES, MindReaderCategory.FOOD_DRINK)),
            new Item("Which season do I prefer?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.INSIGHT)),
            new Item("What colour would I paint my room?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FUN)),
            new Item("What's my preferred exercise or physical activity?", List.of(MindReaderCategory.FAVOURITES, MindReaderCategory.SPORTS)),
            new Item("What is my favourite movie?", List.of(MindReaderCategory.FAVOURITES, MindReaderCategory.FUN, MindReaderCategory.HOBBIES)),
            new Item("What is my favourite game?", List.of(MindReaderCategory.FAVOURITES, MindReaderCategory.HOBBIES)),
            new Item("Who is my favourite sports team?", List.of(MindReaderCategory.FAVOURITES, MindReaderCategory.SPORTS)),
            new Item("What is my favourite song?", List.of(MindReaderCategory.FAVOURITES, MindReaderCategory.FUN)),
            new Item("Am I right or left handed?", List.of(MindReaderCategory.INSIGHT, MindReaderCategory.PHYSICAL)),
            new Item("What was my childhood dream?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.CHILDHOOD, MindReaderCategory.MEMORIES)),
            new Item("Where would you find me on my day off?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FUN, MindReaderCategory.PERSONALITY)),
            new Item("If we go out to eat, am I more likely to try something new or order the same thing I like?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FOOD_DRINK, MindReaderCategory.PERSONALITY)),
            new Item("Am I a morning person or a night owl?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.GENERAL, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather watch a movie at home or go to the cinema?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FUN, MindReaderCategory.HOBBIES)),
            new Item("If I had a day off, would I rather relax or do something productive?", List.of(MindReaderCategory.GENERAL, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather go on a road trip or fly somewhere far away?", List.of(MindReaderCategory.FUN, MindReaderCategory.PERSONALITY, MindReaderCategory.TRAVEL)),
            new Item("Do I prefer sweet snacks or salty snacks?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FOOD_DRINK, MindReaderCategory.FUN)),
            new Item("Would I rather read a book or watch a series?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.HOBBIES, MindReaderCategory.PERSONALITY)),
            new Item("Am I more likely to plan everything or go with the flow?", List.of(MindReaderCategory.GENERAL, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather spend money on experiences or things?", List.of(MindReaderCategory.GENERAL, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("If we play a board game, am I very competitive or just playing for fun?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FUN, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather go to a big party or a small gathering?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FUN, MindReaderCategory.PERSONALITY)),
            new Item("If I start a hobby, am I likely to stick with it or move on quickly?", List.of(MindReaderCategory.HOBBIES, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather learn a musical instrument or a new language?", List.of(MindReaderCategory.FUN, MindReaderCategory.HOBBIES, MindReaderCategory.INSIGHT)),
            new Item("If I'm on holiday, do I prefer relaxing or exploring all day?", List.of(MindReaderCategory.FUN, MindReaderCategory.GENERAL, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather cook a meal myself or order takeaway?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FOOD_DRINK, MindReaderCategory.PERSONALITY)),
            new Item("Do I prefer working alone or with a group?", List.of(MindReaderCategory.GENERAL, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("Am I more likely to arrive early or right on time?", List.of(MindReaderCategory.GENERAL, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("Do I prefer hot weather or cold weather?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.GENERAL, MindReaderCategory.PERSONALITY)),
            new Item("Do I prefer individual or team sports?", List.of(MindReaderCategory.PERSONALITY, MindReaderCategory.PHYSICAL, MindReaderCategory.SPORTS)),
            new Item("Do I prefer short intense workouts or longer relaxed ones?", List.of(MindReaderCategory.PERSONALITY, MindReaderCategory.PHYSICAL, MindReaderCategory.SPORTS)),
            new Item("Would I rather spend a weekend in the city or in nature?", List.of(MindReaderCategory.FUN, MindReaderCategory.GENERAL, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather watch live sports or highlights later?", List.of(MindReaderCategory.CASUAL, MindReaderCategory.FUN, MindReaderCategory.SPORTS)),
            new Item("Would I rather win a competition or have everyone enjoy the game?", List.of(MindReaderCategory.FUN, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("Do I prefer structured routines or flexible days?", List.of(MindReaderCategory.GENERAL, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather spend time improving a skill or trying lots of different things?", List.of(MindReaderCategory.HOBBIES, MindReaderCategory.INSIGHT, MindReaderCategory.PERSONALITY)),
            new Item("Would I rather watch a comedy or something thought-provoking?", List.of(MindReaderCategory.FUN, MindReaderCategory.HOBBIES, MindReaderCategory.PERSONALITY))
    );
}
