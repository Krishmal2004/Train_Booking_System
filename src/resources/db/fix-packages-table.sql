USE train_system;

-- Check if both columns exist
SELECT
    COUNT(*) AS column_count,
    SUM(CASE WHEN COLUMN_NAME = 'package_id' THEN 1 ELSE 0 END) AS has_package_id,
    SUM(CASE WHEN COLUMN_NAME = 'packageid' THEN 1 ELSE 0 END) AS has_packageid
FROM
    INFORMATION_SCHEMA.COLUMNS
WHERE
    TABLE_SCHEMA = 'train_system'
    AND TABLE_NAME = 'packages'
    AND (COLUMN_NAME = 'package_id' OR COLUMN_NAME = 'packageid');

-- If packageid exists and package_id exists, drop packageid column
-- If only packageid exists, rename it to package_id
-- If only package_id exists, do nothing

-- First try to drop packageid if it exists (along with package_id)
SET @query = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = 'train_system'
     AND TABLE_NAME = 'packages'
     AND COLUMN_NAME = 'packageid') > 0
    AND
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = 'train_system'
     AND TABLE_NAME = 'packages'
     AND COLUMN_NAME = 'package_id') > 0,
    'ALTER TABLE packages DROP COLUMN packageid;',
    'SELECT "No duplicate column found, no need to drop packageid."'
);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- If only packageid exists but package_id doesn't, rename it
SET @query = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = 'train_system'
     AND TABLE_NAME = 'packages'
     AND COLUMN_NAME = 'packageid') > 0
    AND
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = 'train_system'
     AND TABLE_NAME = 'packages'
     AND COLUMN_NAME = 'package_id') = 0,
    'ALTER TABLE packages CHANGE packageid package_id VARCHAR(20) NOT NULL;',
    'SELECT "No need to rename packageid to package_id."'
);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Verify the result
SELECT COLUMN_NAME, ORDINAL_POSITION, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'train_system' AND TABLE_NAME = 'packages'
ORDER BY ORDINAL_POSITION;

