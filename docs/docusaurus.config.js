const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'MCDash',
  tagline: 'MCDash is a simple dashboard for your Minecraft server.',
  favicon: 'img/favicon.ico',
  url: 'https://mcdash.gnmyt.dev',
  baseUrl: '/',
  organizationName: 'gnmyt',
  projectName: 'MCDash',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  i18n: {defaultLocale: 'en', locales: ['en']},
  presets: [
    ['classic', ({
        docs: {sidebarPath: require.resolve('./sidebars.js'),
            editUrl: 'https://github.com/gnmyt/MCDash/tree/master/docs/'},
        theme: {customCss: require.resolve('./src/css/custom.css')},
      }),
    ],
  ],

  themeConfig:
    ({
      navbar: {
        title: 'MCDash',
        logo: {alt: 'MCDash', src: 'img/favicon.png'},
        items: [
          {type: 'docSidebar', sidebarId: 'default', position: 'left', label: 'Documentation'},
          {href: 'https://github.com/gnmyt/MCDash', label: 'GitHub', position: 'right'},
        ],
      },
      footer: {
        style: 'dark',
        copyright: `Copyright © ${new Date().getFullYear()} MCDash. Built with Docusaurus.`,
      },
      prism: {theme: lightCodeTheme, darkTheme: darkCodeTheme},
    }),
};

module.exports = config;
