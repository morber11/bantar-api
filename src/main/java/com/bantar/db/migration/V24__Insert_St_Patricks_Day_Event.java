package com.bantar.db.migration;

import com.bantar.db.tools.DatabaseMigrationHelper;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.Date;

import static com.bantar.db.tools.DatabaseMigrationHelper.row;

@SuppressWarnings("unused")
public class V24__Insert_St_Patricks_Day_Event extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        Connection connection = context.getConnection();

        long eventId = DatabaseMigrationHelper.insertIntoTableWithIdentity(connection, "EVENT",
                "NAME, FRIENDLY_NAME, TYPE, FROM_DATE, UNTIL_DATE, STYLE",
                "ST_PATRICKS_DAY", "St Patrick's Day", 0, Date.valueOf("2026-03-16"), Date.valueOf("2026-03-20"),
                "{ \"style\": { \"light\": \"background-color: #0d9488;\", \"dark\": \"background-color: #134e4a;\" }}");

        DatabaseMigrationHelper.batchInsertIntoTable(connection, "EVENT_QUESTIONS", "TEXT, EVENT_ID",
                row("Where did St. Patrick originally come from?", eventId),
                row("Are you wearing green on St. Patrick's Day? Why or why not?", eventId),
                row("What was the most memorable St. Patrick's Day parade you've experienced?", eventId),
                row("What is the best traditional Irish food?", eventId),
                row("Barry's or Lyons?", eventId),
                row("What is the best Irish band or musician?", eventId),
                row("What are some of the biggest misconceptions about Ireland?", eventId),
                row("What is the worst town in Ireland and why?", eventId),
                row("What are some of the best Irish made movies or television shows?", eventId),
                row("If you could be on the Late Late Toy Show, would you?", eventId),
                row("What is your favorite seanfhocail?", eventId),
                row("What is the best chippy you've ever had?", eventId),
                row("What toppings do you get on your chicken fillet roll?", eventId),
                row("What is the best Irish slang word or phrase?", eventId),
                row("Do you drink Guinness? Why or why not?", eventId),
                row("Best way to eat a potato? (Mashed, Baked, Roast, Steamed, etc...)", eventId),
                row("What's your favorite Irish myth or legend?", eventId),
                row("What's the most overrated tourist spot in Ireland?", eventId),
                row("If you could live anywhere in Ireland, where would you live?", eventId),
                row("What stereotypically Irish thing is overrated?", eventId),
                row("Tayto or Kings?", eventId),
                row("What is the worst Irish slang you've heard?", eventId),
                row("What is a popular invention that was made by an Irish person?", eventId),
                row("Should there be more native trees planted in Ireland? Why or why not?", eventId),
                row("Do Irish people have a good reputation abroad?", eventId),
                row("How often do you commit to an Irish goodbye?", eventId),
                row("How good is your Gaeilge?", eventId),
                row("What is the luckiest thing that has ever happened to you?", eventId),
                row("How full is your full Irish breakfast?", eventId),
                row("Bachelors or Heinz?", eventId),
                row("If you found a pot of gold, what would you do with it?", eventId),
                row("How often do you go to mass?", eventId),
                row("What Irish accent do you hate?", eventId),
                row("Have you ever been Irish Dancing (céilí)?", eventId),
                row("What Irish stereotype is completely inaccurate?", eventId),
                row("What is more iconic: the shamrock, or the harp?", eventId),
                row("What is commonly associated with Ireland or Irish people, but has nothing to do with it?", eventId),
                row("What is one Irish food everyone should try at least once?", eventId),
                row("Have you ever been to a popular Irish tourist spot? (e.g: Cliffs of Moher, The Giant's Causeway)", eventId),
                row("What's the craic?", eventId));

        DatabaseMigrationHelper.setIdentitySequence(connection, "EVENT", "ID");
        DatabaseMigrationHelper.setIdentitySequence(connection, "EVENT_QUESTIONS", "ID");

    }
}
