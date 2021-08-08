from airML import airML
import unittest
import json


class TestAirML(unittest.TestCase):
    def test_list(self):
        output = airML.execute('list -o json')
        output = json.loads(output)
        self.assertTrue(isinstance(output, dict))
        self.assertEqual(output['status_code'], 200)

    def test_list_kns(self):
        output = airML.execute('list kns -o json')
        output = json.loads(output)
        self.assertTrue(isinstance(output, dict))
        self.assertEqual(output['status_code'], 200)

    def test_info(self):
        output = airML.execute("info http://purl.org/pcp-on-web/dbpedia -o json")
        output = json.loads(output)
        self.assertTrue(isinstance(output, dict))
        self.assertEqual(output['status_code'], 200)

    def test_search(self):
        output = airML.execute('search ontology -o json')
        output = json.loads(output)
        self.assertTrue(isinstance(output, dict))
        self.assertEqual(output['status_code'], 200)

    def test_dir(self):
        output = airML.execute('r-dir -o json')
        output = json.loads(output)
        self.assertTrue(isinstance(output, dict))
        self.assertEqual(output['status_code'], 200)

    def test_version(self):
        output = airML.execute('-version -o json')
        output = json.loads(output)
        self.assertTrue(isinstance(output, dict))
        self.assertEqual(output['status_code'], 200)

    def test_invalid_command(self):
        output = airML.execute('lists')
        self.assertTrue("KBox.jar <command> [option]" in output)


if __name__ == '__main__':
    unittest.main()
