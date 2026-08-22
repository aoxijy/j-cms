INSERT INTO site_properties (property_order, property_label, property_name, property_value)
VALUES (10, 'Admin language', 'admin.language', 'en')
ON CONFLICT (property_name) DO NOTHING;

INSERT INTO site_properties (property_order, property_label, property_name, property_value)
VALUES (20, 'Admin color theme', 'admin.theme', 'default')
ON CONFLICT (property_name) DO NOTHING;
