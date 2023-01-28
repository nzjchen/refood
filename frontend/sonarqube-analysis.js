const sonarqubeScanner =  require('sonarqube-scanner');
sonarqubeScanner(
    {
        serverUrl:  'https://csse-s302g9.canterbury.ac.nz/sonarqube/',
        token: "d4e9b98916e388ef5dee9b215b83962045deffd2",
        options : {
            'sonar.projectKey': 'team-900-client',
            'sonar.projectName': 'Team 900 - Client',
            "sonar.sourceEncoding": "UTF-8",
            'sonar.sources': 'src',
            'sonar.tests': 'src',
            'sonar.inclusions': '**',
            'sonar.test.inclusions': 'src/**/*.spec.js,src/**/*.test.js,src/**/*.test.ts, src/test',
            'sonar.typescript.lcov.reportPaths': 'coverage/lcov.info',
            'sonar.javascript.lcov.reportPaths': 'coverage/lcov.info',
            'sonar.testExecutionReportPaths': 'coverage/test-reporter.xml'
        }
    }, () => {});
