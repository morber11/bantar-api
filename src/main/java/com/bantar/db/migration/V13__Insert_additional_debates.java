package com.bantar.db.migration;

import com.bantar.db.tools.DatabaseMigrationHelper;
import com.bantar.model.DebateCategory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class V13__Insert_additional_debates extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();

        // Ensure identity sequences won't collide with generated-key inserts
        DatabaseMigrationHelper.setIdentitySequence(connection, "DEBATE", "ID");
        DatabaseMigrationHelper.setIdentitySequence(connection, "DEBATE_CATEGORY", "DEBATE_CATEGORY_ID");

        String selectDebateSql = "SELECT ID FROM DEBATE WHERE TEXT = ?";
        String insertDebateSql = "INSERT INTO DEBATE (TEXT) VALUES (?)";
        String selectCategorySql = "SELECT COUNT(*) FROM DEBATE_CATEGORY WHERE DEBATE_ID = ? AND CATEGORY_CODE = ?";
        String insertCategorySql = "INSERT INTO DEBATE_CATEGORY (DEBATE_ID, CATEGORY_CODE) VALUES (?, ?)";

        Map<String, List<String>> debates = buildDebates();

        try (PreparedStatement selectDebateStmt = connection.prepareStatement(selectDebateSql);
             PreparedStatement insertDebateStmt = connection.prepareStatement(insertDebateSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement selectCategoryStmt = connection.prepareStatement(selectCategorySql);
             PreparedStatement insertCategoryStmt = connection.prepareStatement(insertCategorySql)) {

            for (Map.Entry<String, List<String>> e : debates.entrySet()) {
                String text = e.getKey();
                List<String> categories = e.getValue();

                int debateId = -1;
                selectDebateStmt.setString(1, text);
                try (ResultSet rs = selectDebateStmt.executeQuery()) {
                    if (rs.next()) {
                        debateId = rs.getInt(1);
                    }
                }

                if (debateId == -1) {
                    insertDebateStmt.setString(1, text);
                    insertDebateStmt.executeUpdate();
                    try (ResultSet gen = insertDebateStmt.getGeneratedKeys()) {
                        if (gen.next()) {
                            debateId = gen.getInt(1);
                        }
                    }
                }

                if (debateId == -1) {
                    throw new IllegalStateException("Failed to obtain debate id for text: " + text);
                }

                for (String category : categories) {
                    selectCategoryStmt.setInt(1, debateId);
                    selectCategoryStmt.setString(2, category);
                    try (ResultSet crs = selectCategoryStmt.executeQuery()) {
                        if (crs.next() && crs.getInt(1) == 0) {
                            insertCategoryStmt.setInt(1, debateId);
                            insertCategoryStmt.setString(2, category);
                            insertCategoryStmt.addBatch();
                        }
                    }
                }
            }

            insertCategoryStmt.executeBatch();
        }
    }

    private Map<String, List<String>> buildDebates() {
        Map<String, List<String>> m = new HashMap<>();

        // Handcrafted debates (100 unique items) — no phrasing templates are used.
        String[] debates = new String[]{
                "Remote work should be a legal right for employees in high-cost cities.",
                "Social media platforms must be required to label AI-generated political content.",
                "A universal basic income provides a better safety net than current welfare systems.",
                "Electric vehicle purchases should receive direct government subsidies.",
                "Using gene drives to control invasive species is an acceptable environmental tool.",
                "Schools should prioritize STEM education over arts and humanities.",
                "Internet access is a basic human right and should be provided universally.",
                "AI-generated art should be eligible for copyright protection under current law.",
                "The voting age should be lowered to sixteen in national elections.",
                "Commercial space mining should be open to private companies under international law.",
                "Public transport should be free in major metropolitan areas.",
                "Standardized testing remains a necessary measure for educational accountability.",
                "Large technology companies should be broken up to restore market competition.",
                "Consumers should have a legal right to repair their electronic devices.",
                "Companies must disclose climate risks in their financial reporting.",
                "Cryptocurrencies should be regulated as legal tender by national governments.",
                "Facial recognition technology should be banned from public law enforcement use.",
                "A four-day work week increases productivity and should be adopted widely.",
                "Governments should subsidize plant-based alternatives to reduce emissions.",
                "Mandatory vaccination policies are justified during severe public health crises.",
                "Student loan debt should be forgiven to broaden access to higher education.",
                "Paid parental leave should be mandated for all employers above a size threshold.",
                "Algorithmic hiring tools require independent auditing before use.",
                "Universal healthcare should be funded through progressive taxation.",
                "Cities should eliminate parking minimums to encourage sustainable development.",
                "Net neutrality protections must be reinstated and enforced.",
                "Governments should regulate influencer advertising aimed at minors.",
                "The gig economy should provide minimum benefits and protections to workers.",
                "Automation should be taxed to fund retraining programs for displaced workers.",
                "AI systems used for creative works should disclose human contribution levels.",
                "Research into climate geoengineering should be publicly funded and regulated.",
                "Decriminalizing certain low-risk drugs would reduce incarceration rates.",
                "Single-use plastics should be phased out through legislation and incentives.",
                "Public broadband access should be expanded as public infrastructure.",
                "Mandatory labeling should identify media that is fully or partially AI-generated.",
                "Corporate boards must meet minimum diversity requirements by law.",
                "Cities should implement congestion pricing to reduce traffic and pollution.",
                "Governments should require carbon border adjustment tariffs to protect industry.",
                "Private companies should not be allowed to collect biometric data without consent.",
                "The state should provide universal childcare to support working families.",
                "Schools should delay start times to match adolescent sleep patterns.",
                "Public funding for the arts is essential and should be increased.",
                "Phasing out internal combustion engines should be mandatory over a set timetable.",
                "Companies should be required to report and reduce microplastic pollution.",
                "Online platforms must take stronger action against misinformation.",
                "Cashless societies risk excluding vulnerable populations and should be avoided.",
                "Citizens should be able to opt out of targeted political advertising.",
                "Legal frameworks must be created for regulation of smart home data collection.",
                "A national AI safety agency should oversee high-risk AI deployments.",
                "Public funding should prioritize space exploration projects with public benefit.",
                "The right to repair medical devices should be protected to improve healthcare access.",
                "Governments should mandate circular product design to reduce waste.",
                "Limits on government data retention are necessary to protect privacy.",
                "Mandatory ID for social media would improve accountability at the cost of privacy.",
                "Taxing meat could be an effective policy to reduce environmental harm.",
                "Reforming pharmaceutical pricing is necessary to ensure medicines are affordable.",
                "Ethical limits should govern the use of drones in public spaces.",
                "Countries should adopt uniform rules for AI transparency in public services.",
                "Public utilities should remain under public control rather than privatized.",
                "Governing bodies should protect indigenous languages through formal programs.",
                "Mandatory corporate reporting on climate risks should be audited externally.",
                "Regulations should prevent microtargeting in political campaigns.",
                "Governments should incentivize renewable energy via targeted subsidies.",
                "Limits on surveillance advertising are necessary to protect consumers.",
                "Legal recognition of digital identities must balance convenience and security.",
                "Mandatory labeling for environmental impact should apply to consumer products.",
                "Schools should teach digital literacy as part of the core curriculum.",
                "Publicly funded research should prioritize biodiversity and conservation efforts.",
                "Government intervention is required to curb the worst effects of fast fashion.",
                "National service should be an option but not a legal requirement for citizens.",
                "Establishing a universal digital ID offers benefits but risks centralization of power.",
                "Algorithms used in policing should be subject to public oversight and transparency.",
                "Governments should support the transition to plant-based agricultural subsidies.",
                "Commercialization of heritage sites should be limited to preserve cultural value.",
                "Mandatory corporate climate disclosures should be standardized internationally.",
                "Education systems should reduce emphasis on standardized tests and increase project-based learning.",
                "Regulations are needed to prevent exploitative data collection by smart devices.",
                "Public policy should encourage citizen assemblies for major ethical questions.",
                "Restrictions on workplace surveillance protect employee privacy and dignity.",
                "Phasing out certain pesticide uses is necessary to protect pollinators and biodiversity.",
                "Financial transaction taxes could reduce speculative trading without harming growth.",
                "Governments should provide tax incentives for companies that adopt circular design.",
                "Banning microtargeted political ads would improve democratic discourse.",
                "Cities should require minimum green space per resident to improve quality of life.",
                "Public access to high-quality broadband should be guaranteed as a public good.",
                "Legislation should limit the length and scope of software patents to foster innovation.",
                "Companies with monopolistic power should be subject to stricter consumer protections.",
                "Governments should fund retraining programs for workers displaced by automation.",
                "Research into safe climate interventions should proceed under strict international oversight.",
                "Regulations should reduce single-use packaging across retail industries.",
                "Public debate should determine the ethical boundaries for human gene editing.",
                "Civil society should have stronger roles in shaping platform content moderation rules."
        };

        for (String debate : debates) {
            m.put(debate, categoriesForTopic(debate));
        }

        return m;
    }

    private List<String> categoriesForTopic(String topic) {
        String t = topic.toLowerCase();
        List<String> cats = new ArrayList<>();

        if (t.contains("education") || t.contains("school") || t.contains("student") || t.contains("testing") || t.contains("tuition")) {
            cats.add(DebateCategory.EDUCATION.name());
        }
        if (t.contains("tech") || t.contains("ai") || t.contains("algorithm") || t.contains("digital") || t.contains("online") || t.contains("cryptocurrency") || t.contains("software")) {
            cats.add(DebateCategory.TECHNOLOGY.name());
        }
        if (t.contains("climate") || t.contains("environment") || t.contains("carbon") || t.contains("plastic") || t.contains("biodiversity") || t.contains("renewable")) {
            cats.add(DebateCategory.ENVIRONMENT.name());
        }
        if (t.contains("ethic") || t.contains("gene") || t.contains("animal") || t.contains("ethical")) {
            cats.add(DebateCategory.ETHICS.name());
        }
        // Policy/category fallback
        if (cats.isEmpty()) {
            cats.add(DebateCategory.POLICY.name());
        }

        // Always include DEBATE as a general category for discoverability
        if (!cats.contains("DEBATE")) {
            cats.add(0, "DEBATE");
        }

        return cats;
    }
}
