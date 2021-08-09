from kbox import kbox
import unittest


class TestAirML(unittest.TestCase):
    def test_list(self):
        output = kbox.execute('-list')
        self.assertTrue('KBox KNS Resource table list' in output)

    def test_info(self):
        output = kbox.execute("-info http://purl.org/pcp-on-web/dbpedia")
        print(output)
        self.assertTrue('KB:http://purl.org/pcp-on-web/dbpedia' in output)

    def test_search(self):
        output = kbox.execute('-search ontology')
        print(output)
        self.assertTrue('KBox KNS Resource table list' in output)

    def test_dir(self):
        output = kbox.execute('-r-dir')
        print(output)
        self.assertTrue('Your current resource directory is:' in output)

    def test_version(self):
        output = kbox.execute('-version')
        print(output)
        self.assertTrue('KBox version v0.0.2-alpha' in output)

    def test_invalid_command(self):
        output = kbox.execute('lists')
        self.assertTrue("KBox.jar <command> [option]" in output)


if __name__ == '__main__':
    unittest.main()
