
CREATE INDEX IF NOT EXISTS idx_visits_visited_at
    ON website_visits (visited_at);

CREATE INDEX IF NOT EXISTS idx_visits_visitor_id
    ON website_visits (visitor_id);
