export type EmailType = 'PHISHING' | 'NORMAL' | 'NOT_SELECT';
export type GenerateBy = 'ADMIN' | 'AI';

export interface AppUser {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  totalScore: number;
  monthlyScore: number;
  weeklyScore: number;
  createdAt: string;
}

export interface ResponseEmailDto {
  emailId: string;
  generateBy: GenerateBy;
  emailType: EmailType;
  userChoice: EmailType;
  userId: string;
  emailName: string;
  senderAddress: string;
  emailTitle: string;
  emailBody: string;
  submitted: boolean;
  createdAt: string;
  submittedAt: string | null;
}

export interface UserRequestedEmailResponse {
  emailId: string;
  emailType: EmailType;
  userChoice: EmailType;
  userId: string;
  emailName: string;
  senderAddress: string;
  emailTitle: string;
  emailBody: string;
  submitted: boolean;
  createdAt: string;
  submittedAt: string | null;
}

export interface SubmitAnswerRequest {
  emailId: string;
  userId: string;
  userChoice: EmailType;
}

export interface SubmitAnswerResponse {
  emailId: string;
  response: string;
  userChoice: EmailType;
  userId: string;
  isAlreadySubmitted: boolean;
  isCorrect: boolean;
}

export interface UserLeaderboardResponse {
  rank: number;
  fullName: string;
  weeklyScore: number;
  monthlyScore: number;
  totalScore: number;
}

export interface AdminLeaderboardResponse {
  rank: number;
  userId: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
  weeklyScore: number;
  monthlyScore: number;
  totalScore: number;
  createdAt: string;
}

export interface MyLeaderboardStatsResponse {
  fullName: string;
  weeklyRank: number;
  weeklyPoints: number;
  weeklyCorrect: number;
  weeklyWrong: number;
  monthlyRank: number;
  monthlyPoints: number;
  monthlyCorrect: number;
  monthlyWrong: number;
  allTimeRank: number;
  allTimePoints: number;
  allTimeCorrect: number;
  allTimeWrong: number;
}

export interface Reward {
  id: string;
  userId: string;
  emailId: string;
  points: number;
  createdAt: string;
}

export interface AdminCreateEmailRequest {
  emailType: EmailType;
  senderAddress: string;
  emailTitle: string;
  emailBody: string;
  link: string;
  createdAt: string;
}

export interface AdminCreatedEmailResponse {
  emailId: string;
  generateBy: GenerateBy;
  emailType: EmailType;
  senderAddress: string;
  emailTitle: string;
  emailBody: string;
  link: string;
  createdAt: string;
}
