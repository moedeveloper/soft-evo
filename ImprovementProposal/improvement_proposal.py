import os
import re


class StringResourceExtractor:
    def __init__(self):
        # Dict holding all extracted values, key is the new reference identifier and value is the extracted string
        self.new_string_resource_entries = {}

    # Replaces hardcoded strings with references to strings.xml resources
    def extract_resources(self, layout_directory_path):
        # Retrieve all .xml files in the provided layout directory
        xml_file_names = [file for file in os.listdir(layout_directory_path) if re.fullmatch(".*\.xml$", file)]

        # Search through each file and look for hardcoded strings
        for xml_file_name in xml_file_names:
            print("Checking {} ...".format(xml_file_name))
            file = open("{}/{}".format(layout_directory_path, xml_file_name), "rU", encoding="UTF-8")
            file_content = file.read()
            file.close()
            corrected_layout = re.sub("(android:text=)(\"(?!@string/).*?\")", self.replace_hardcoded_string, file_content)
            with open("{}/{}".format(layout_directory_path, xml_file_name), "w", encoding="UTF-8") as f:
                print(corrected_layout, file=f)

    # Takes a regex match object containing a hardcoded string, prompts the user for a resource name and returns the
    # string that will reference the new string resource.
    def replace_hardcoded_string(self, match_object):
        string = match_object.group(2)
        print("Hardcoded string found:\n{}".format(string))
        resource_name = input("Enter a name for the new string resource: ")
        self.new_string_resource_entries[resource_name] = string.strip("\"")  # TODO: Check that it is available
        return "{}\"@string/{}\"".format(match_object.group(1), resource_name)

    # Updates the strings.xml files with all extracted resources
    def update_strings_file(self, file_path):
        file = open(file_path)
        file_contents = file.readlines()
        file.close()
        
        insertion_index = 0
        for i in range(0, len(file_contents)):
            if re.search("</resources>", file_contents[i]):
                insertion_index = i

        for k, v in self.new_string_resource_entries.items():
            file_contents.insert(insertion_index, '    <string name="{}">{}</string>\n'.format(k, v))

        file_contents = "".join(file_contents)
        file = open(file_path, "w")
        file.write(file_contents)
        file.close()


class Main:
    # Prompt the user for the relevant directory paths
    layout_directory_path = input("Enter the layout directory path: ")
    strings_xml_directory_path = input("Enter the path to the directory containing strings.xml: ")

    sre = StringResourceExtractor()
    sre.extract_resources(layout_directory_path)
    sre.update_strings_file("{}/strings.xml".format(strings_xml_directory_path))



