package edu.nsbm.phishguard.util.constant;

public class Prompt {

    private Prompt() {
    }

    public static final String FAKE_MAIL_SYSTEM = "You generate fake phishing emails for security training. Pick a random famous company (Google, Microsoft, Facebook, Apple, Amazon, PayPal, Netflix, DHL, LinkedIn, Dropbox, WhatsApp, Instagram, Spotify, Adobe, Zoom, Slack, GitHub, Bank of America, Wells Fargo, HSBC, or any other). Create a fake domain similar to real one. Return ONLY JSON with exactly these 4 fields: sender_email, title, body, link";
    public static final String FAKE_MAIL_USER = "Generate a phishing email impersonating a random famous company. Use a fake domain that looks like the real one but is slightly different (example: micros0ft-security.com, faceb00k-support.net, arnazon.com, app1e-id.com, pay-pal-verify.com). Return ONLY: sender_email (fake), title (email subject), body (email content), link (suspicious URL)";

    public static final String NORMAL_MAIL_SYSTEM = "You generate realistic everyday emails for security awareness training. Pick a random famous company (Google, Microsoft, Amazon, PayPal, Netflix, LinkedIn, Dropbox, Spotify, Adobe, Zoom, Slack, GitHub, or any other). Use their real official domain. Return ONLY JSON with exactly these 4 fields: sender_email, title, body, link";
    public static final String NORMAL_MAIL_USER = "Generate a legitimate-looking everyday email from a real famous company. Use their correct official domain (example: google.com, microsoft.com, amazon.com, paypal.com). The email should be about something normal like a newsletter, order confirmation, account activity summary, new feature announcement, or subscription renewal notice. Return ONLY: sender_email (real official domain), title (email subject), body (email content), link (real official URL)";

}
