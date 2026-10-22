package com.bantar.seeder;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventSeeder implements Seeder {

    record EventSeed(String name, String friendlyName, int type, LocalDate fromDate,
                     LocalDate untilDate, String style, List<String> questions) {
    }

    private static final List<EventSeed> EVENTS = List.of(
            new EventSeed("ST_PATRICKS_DAY", "St Patrick's Day", 0,
                    LocalDate.of(2026, 3, 16), LocalDate.of(2026, 3, 20),
                    "{ \"style\": { \"light\": \"background-color: #0d9488;\", \"dark\": \"background-color: #134e4a;\" }}",
                    List.of(
                            "Where did St. Patrick originally come from?",
                            "Are you wearing green on St. Patrick's Day? Why or why not?",
                            "What was the most memorable St. Patrick's Day parade you've experienced?",
                            "What is the best traditional Irish food?",
                            "Barry's or Lyons?",
                            "What is the best Irish band or musician?",
                            "What are some of the biggest misconceptions about Ireland?",
                            "What is the worst town in Ireland and why?",
                            "What are some of the best Irish made movies or television shows?",
                            "If you could be on the Late Late Toy Show, would you?",
                            "What is your favourite seanfhocail?",
                            "What is the best chippy you've ever had?",
                            "What toppings do you get on your chicken fillet roll?",
                            "What is the best Irish slang word or phrase?",
                            "Do you drink Guinness? Why or why not?",
                            "Best way to eat a potato? (Mashed, Baked, Roast, Steamed, etc...)",
                            "What's your favourite Irish myth or legend?",
                            "What's the most overrated tourist spot in Ireland?",
                            "If you could live anywhere in Ireland, where would you live?",
                            "What stereotypically Irish thing is overrated?",
                            "Tayto or Kings?",
                            "What is the worst Irish slang you've heard?",
                            "What is a popular invention that was made by an Irish person?",
                            "Should there be more native trees planted in Ireland? Why or why not?",
                            "Do Irish people have a good reputation abroad?",
                            "How often do you commit to an Irish goodbye?",
                            "How good is your Gaeilge?",
                            "What is the luckiest thing that has ever happened to you?",
                            "How full is your full Irish breakfast?",
                            "Bachelors or Heinz?",
                            "If you found a pot of gold, what would you do with it?",
                            "How often do you go to mass?",
                            "What Irish accent do you hate?",
                            "Have you ever been Irish Dancing (c\u00e9il\u00ed)?",
                            "What Irish stereotype is completely inaccurate?",
                            "What is more iconic: the shamrock, or the harp?",
                            "What is commonly associated with Ireland or Irish people, but has nothing to do with it?",
                            "What is one Irish food everyone should try at least once?",
                            "Have you ever been to a popular Irish tourist spot? (e.g: Cliffs of Moher, The Giant's Causeway)",
                            "What's the craic?"
                    )),
            new EventSeed("HALLOWEEN", "Halloween", 0,
                    LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 31),
                    "{ \"style\": { \"light\": \"background-color: #ea580c;\", \"dark\": \"background-color: #431407;\" }}",
                    List.of(
                            "What is the best/worst Halloween costume you've ever seen or worn?",
                            "What is the best/worst thing to get in your trick or treat bag?",
                            "What is the scariest film you've ever watched?",
                            "Do you believe in ghosts? Have you ever had a scary experience you can't explain?",
                            "If your house was haunted, would you stay or leave?",
                            "What is the best/worst Halloween movie of all time?",
                            "Would you rather spend a night in a haunted house or a graveyard?",
                            "What is your biggest fear, rational or irrational?",
                            "Did you go trick or treating as a kid? What was your best ever haul?",
                            "What is an underrated horror villain?",
                            "Do you prefer jump scares or slow-burn horror?",
                            "What is the best/worst Halloween party you've experienced?",
                            "What is the best Halloween decoration you've seen on a house?",
                            "Would you ever dress up for work on Halloween?",
                            "What is the creepiest place you've ever been?",
                            "What is your favourite type of ghost?",
                            "What are the origins of Halloween?",
                            "Why are there no caveman ghosts?",
                            "Who would win in a fight between classic horror monsters (Dracula, Frankenstein, The Mummy, The Wolfman, etc.)?",
                            "What is your favourite horror movie and why?",
                            "Do you prefer if horror movies are ambigious or if they explain everything?",
                            "What is the best/worst horror video game you've played?",
                            "What is a monster that you personally find not scary?",
                            "What is an unusual thing that you personally find terrifying?",
                            "Does Christmas overshadow Halloween? Why or why not?",
                            "How much effort do you put in for Halloween?",
                            "What is the most outrageous Halloween costume you've ever seen someone wear?",
                            "How would you personally defeat Dracula?",
                            "What type of zombie do you find the scariest? (Slow, Braaains, Fast, Intelligent, etc...)"                    
                    )))
    ;

    private final SeederJdbcHelper helper;

    public EventSeeder(SeederJdbcHelper helper) {
        this.helper = helper;
    }

    @Override
    public void seed() {
        for (EventSeed event : EVENTS) {
            long eventId = helper.findOrInsertEvent(event.name(), event.friendlyName(), event.type(),
                    event.fromDate(), event.untilDate(), event.style());
            for (String question : event.questions()) {
                helper.insertEventQuestion(eventId, question);
            }
        }
    }
}
