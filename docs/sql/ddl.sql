CREATE TABLE IF NOT EXISTS `game`
(
    `game_id`     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `service_key` TEXT                              NOT NULL,
    `created`     INTEGER                           NOT NULL,
    `pool`        TEXT                              NOT NULL,
    `pool_size`   INTEGER                           NOT NULL,
    `length`      INTEGER                           NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_game_service_key` ON `game` (`service_key`);

CREATE INDEX IF NOT EXISTS `index_game_created` ON `game` (`created`);

CREATE INDEX IF NOT EXISTS `index_game_pool_size` ON `game` (`pool_size`);

CREATE INDEX IF NOT EXISTS `index_game_length` ON `game` (`length`);

CREATE TABLE IF NOT EXISTS `guess`
(
    `guess_id`      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `game_id`       INTEGER                           NOT NULL,
    `service_key`   TEXT                              NOT NULL,
    `created`       INTEGER                           NOT NULL,
    `text`          TEXT                              NOT NULL,
    `exact_matches` INTEGER                           NOT NULL,
    `near_matches`  INTEGER                           NOT NULL,
    FOREIGN KEY (`game_id`) REFERENCES `game` (`game_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS `index_guess_service_key` ON `guess` (`service_key`);

CREATE INDEX IF NOT EXISTS `index_guess_game_id` ON `guess` (`game_id`);

CREATE INDEX IF NOT EXISTS `index_guess_created` ON `guess` (`created`);

CREATE VIEW `game_summary` AS
SELECT g.*,
       s.guess_count,
       (s.last_guess - s.first_guess) AS total_time
FROM game AS g
         INNER JOIN (
    SELECT game_id,
           COUNT(*)     AS guess_count,
           MIN(created) AS first_guess,
           MAX(created) AS last_guess
    FROM guess
    GROUP BY game_id
) AS s
                    ON g.game_id = s.game_id;